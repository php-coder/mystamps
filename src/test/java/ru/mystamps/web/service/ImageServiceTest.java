/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.testng.Assert.fail;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.fest.assertions.api.Assertions.assertThat;

import ru.mystamps.web.dao.ImageDao;
import ru.mystamps.web.entity.Image;
import ru.mystamps.web.Url;

public class ImageServiceTest {
	
	@Mock
	private ImageDao imageDao;
	
	@Mock
	private Image image;
	
	@Mock
	private MultipartFile multipartFile;
	
	@Captor
	private ArgumentCaptor<Image> imageCaptor;
	
	@InjectMocks
	private ImageService service = new ImageService();
	
	@BeforeMethod
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		final long anyValue = 1024;
		when(multipartFile.getSize()).thenReturn(anyValue);
		when(multipartFile.getContentType()).thenReturn("image/png");
		when(imageDao.save(any(Image.class))).thenReturn(new Image());
	}
	
	//
	// Tests for save()
	//
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void saveShouldThrowExceptionIfFileIsNull() {
		service.save(null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void saveShouldThrowExceptionIfFileHasZeroSize() {
		when(multipartFile.getSize()).thenReturn(0L);
		
		service.save(multipartFile);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void saveShouldThrowExceptionIfContentTypeIsNull() {
		when(multipartFile.getContentType()).thenReturn(null);
		
		service.save(multipartFile);
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void saveShouldThrowExceptionForUnsupportedContentType() {
		when(multipartFile.getContentType()).thenReturn("image/tiff");
		
		service.save(multipartFile);
	}
	
	@Test
	public void saveShouldPassImageToImageDao() {
		service.save(multipartFile);
		
		verify(imageDao).save(any(Image.class));
	}
	
	@Test
	public void saveShouldConvertIoExceptionToRuntimeException() throws IOException {
		when(multipartFile.getBytes()).thenThrow(new IOException());
		
		try {
			service.save(multipartFile);
			
			fail("Expected RuntimeException should be thrown");
			
		} catch (final RuntimeException ex) {
			assertThat(ex.getCause()).isExactlyInstanceOf(IOException.class);
		}
	}
	
	@Test
	public void saveShouldPassFileContentToDao() throws IOException {
		final byte[] expected = "test".getBytes();
		when(multipartFile.getBytes()).thenReturn(expected);
		
		service.save(multipartFile);
		
		verify(imageDao).save(imageCaptor.capture());
		
		assertThat(imageCaptor.getValue().getImage()).isEqualTo(expected);
	}
	
	@Test
	public void saveShouldPassContentTypeToDao() {
		when(multipartFile.getContentType()).thenReturn("image/jpeg");
		
		service.save(multipartFile);
		
		verify(imageDao).save(imageCaptor.capture());
		
		assertThat(imageCaptor.getValue().getType()).isEqualTo(Image.Type.JPEG);
	}
	
	@Test
	public void saveShouldReturnUrlWithImage() {
		final Integer expectedId = 10;
		final String expectedUrl = Url.GET_IMAGE_PAGE.replace("{id}", String.valueOf(expectedId));
		when(image.getId()).thenReturn(expectedId);
		when(imageDao.save(any(Image.class))).thenReturn(image);
		
		final String result = service.save(multipartFile);
		
		assertThat(result).isEqualTo(expectedUrl);
	}
	
	//
	// Tests for findById()
	//
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void findByIdShouldThrowExceptionWhenIdIsNull() {
		service.findById(null);
	}
	
	@Test
	public void findByIdShouldCallImageDao() {
		final Integer anyId = 3;
		service.findById(anyId);
		
		verify(imageDao).findOne(anyInt());
	}
	
	@Test
	public void findByIdShouldPassIdToImageDao() {
		final Integer expectedId = 3;
		
		service.findById(expectedId);
		
		verify(imageDao).findOne(eq(expectedId));
	}
	
	@Test
	public void findByIdShouldReturnValueFromImageDao() {
		final Image expectedImage = getImage();
		final Integer imageId = expectedImage.getId();
		when(imageDao.findOne(eq(imageId))).thenReturn(expectedImage);
		
		final Image actualImage = service.findById(imageId);
		
		assertThat(actualImage).isEqualTo(expectedImage);
	}
	
	private static Image getImage() {
		final Image image = new Image();
		image.setId(1);
		return image;
	}
	
}
