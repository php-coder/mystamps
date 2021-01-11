import React from 'react';
import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom/extend-expect'
import './AddCommentForm.js'

describe('AddCommentFormView', () => {

	it('renders the form', () => {
		// given
		// when
		render(
			<AddCommentFormView />
		);
		// then
		const commentField = screen.getByLabelText('Comment');
		expect(commentField).toBeRequired();

		const submitButton = screen.getByRole('button', {name: 'Add'});
		expect(submitButton).toBeEnabled();
	});

	it('disables the submit button', () => {
		// given
		// when
		render(
			<AddCommentFormView isDisabled={ true } />
		);
		// then
		const submitButton = screen.getByRole('button', {name: 'Add'});
		expect(submitButton).toBeDisabled();
	});

	describe('shows an error for', () => {

		it('a whole form', () => {
			// given
			// when
			render(
				<AddCommentFormView hasServerError={ true } />
			);
			// then
			const alert = screen.getByRole('alert');
			expect(alert).toHaveTextContent('Server error');
			expect(alert).not.toHaveClass('hidden');
		});

		it('the comment field', () => {
			// given
			// when
			const { container } = render(
				<AddCommentFormView validationErrors={ [ 'err1', 'err2' ] } />
			);
			// then
			const form = screen.getByRole('form', {name: 'Add a comment'});
			expect(form).not.toBeNull();
			expect(form).toHaveClass('has-error');

			// @todo #1489 Use toHaveErrorMessage() or toHaveDescription() for checking error messages
			const fieldErrors = container.querySelector('#new-comment\\.errors');
			expect(fieldErrors).not.toBeNull();
			expect(fieldErrors).not.toHaveClass('hidden');
			expect(fieldErrors).toHaveTextContent('err1, err2');
		});
	});

});
