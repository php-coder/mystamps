/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.fest.assertions.api.Assertions.assertThat;

import ru.mystamps.web.dao.ImageDao;
import ru.mystamps.web.entity.Image;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {
	
	@Mock
	private ImageDao imageDao;
	
	@Mock
	private MultipartFile multipartFile;
	
	@Captor
	private ArgumentCaptor<Image> imageCaptor;
	
	@InjectMocks
	private ImageService service = new ImageServiceImpl();
	
	@Before
	public void setUp() {
		final long anyValue = 1024;
		when(multipartFile.getSize()).thenReturn(anyValue);
		when(multipartFile.getContentType()).thenReturn("image/png");
		when(imageDao.save(any(Image.class))).thenReturn(new Image());
	}
	
	//
	// Tests for save()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void saveShouldThrowExceptionIfFileIsNull() {
		service.save(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveShouldThrowExceptionIfFileHasZeroSize() {
		when(multipartFile.getSize()).thenReturn(0L);
		
		service.save(multipartFile);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveShouldThrowExceptionIfContentTypeIsNull() {
		when(multipartFile.getContentType()).thenReturn(null);
		
		service.save(multipartFile);
	}
	
	@Test(expected = IllegalStateException.class)
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
			
		} catch (RuntimeException ex) {
			assertThat(ex.getCause()).isExactlyInstanceOf(IOException.class);
		}
	}
	
	@Test
	public void saveShouldPassFileContentToDao() throws IOException {
		byte[] expected = "test".getBytes();
		when(multipartFile.getBytes()).thenReturn(expected);
		
		service.save(multipartFile);
		
		verify(imageDao).save(imageCaptor.capture());
		
		assertThat(imageCaptor.getValue().getData()).isEqualTo(expected);
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
		String expectedUrl =
			ImageService.GET_IMAGE_PAGE.replace("{id}", String.valueOf(expectedId));
		Image image = mock(Image.class);
		when(image.getId()).thenReturn(expectedId);
		when(imageDao.save(any(Image.class))).thenReturn(image);
		
		String result = service.save(multipartFile);
		
		assertThat(result).isEqualTo(expectedUrl);
	}
	
}
