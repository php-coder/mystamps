//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//
import React from 'react';

// @todo #1338 AddCommentForm: add tests
class AddCommentForm extends React.PureComponent {
	constructor(props) {
		super(props);
		this.state = {
			comment: '',
			validationErrors: [],
			hasServerError: false,
			isDisabled: false
		};
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChange = this.handleChange.bind(this);
	}

	handleChange(event) {
		event.preventDefault();
		this.setState({
			comment: event.target.value
		});
	}

	handleSubmit(event) {
		event.preventDefault();

		this.setState({
			isDisabled: true,
			hasServerError: false,
			validationErrors: []
		});

		axios.patch(
			this.props.url, [
				{
					op: 'add',
					path: '/comment',
					value: this.state.comment
				}
			],
			{
				headers: {
					[this.props.csrfHeaderName]: this.props.csrfTokenValue,
					'Cache-Control': 'no-store'
				},
				validateStatus: status => {
					return status == 204 || status == 400;
				}
			})
			.then(response => {
				const data = response.data;

				if (data.hasOwnProperty('fieldErrors')) {
					const fieldErrors = [];
					if (data.fieldErrors.value) {
						fieldErrors.push(...data.fieldErrors.value);
					}
					this.setState({
						isDisabled: false,
						validationErrors: fieldErrors
					});
					return;
				}

				// no need to reset the state as page will be reloaded
				window.location.reload();
			})
			.catch(error => {
				console.error(error);
				this.setState({ isDisabled: false, hasServerError: true });
			});
	}
	render() {
		return (
			<AddCommentFormView 
				l10n={this.props.l10n}
				handleSubmit={this.handleSubmit}
				hasServerError={this.state.hasServerError}
				handleChange={this.handleChange}
				validationErrors={this.state.validationErrors}
				isDisabled={this.state.isDisabled}
			/>
		);
	}
}

class AddCommentFormView extends React.PureComponent {
	render() {
		const {handleSubmit, hasServerError, handleChange, validationErrors = [], isDisabled, l10n = {}} = this.props;
		const hasValidationErrors = validationErrors.length > 0;
		return (
			<div className="col-sm-12 form-group">
				<form id="add-comment-form"
					aria-label={ l10n['t_add_comment'] || 'Add a comment' }
					className={ `form-horizontal ${hasValidationErrors ? 'has-error' : ''}` }
					onSubmit={ handleSubmit }>
					{ hasServerError &&
						<div id="add-comment-failed-msg"
							role="alert"
							className="alert alert-danger text-center col-sm-8 col-sm-offset-2">
							{ l10n['t_server_error'] || 'Server error' }
						</div>
					}
					<div className="form-group form-group-sm">
						<label htmlFor="new-comment" className="control-label col-sm-3 required-field">
							{ l10n['t_comment'] || 'Comment' }
						</label>
						<div className="col-sm-6">
							<textarea
								id="new-comment"
								className="form-control"
								cols="22"
								rows="3"
								required="required"
								onChange={ handleChange }>
							</textarea>
						</div>
					</div>
					<div className="col-sm-offset-3 col-sm-9">
						{ hasValidationErrors &&
							<span id="new-comment.errors" className="help-block">
								{ validationErrors.join(', ') }
							</span>
						}
						<button
							type="submit"
							className="btn btn-primary btn-sm"
							disabled={ isDisabled }>
							{ l10n['t_add'] || 'Add' }
						</button>
					</div>
				</form>
			</div>
		);
	}
}

window.AddCommentForm = AddCommentForm;
window.AddCommentFormView = AddCommentFormView;
