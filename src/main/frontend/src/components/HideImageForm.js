//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//
import React from 'react';

// @todo #1383 HideImageForm: add tests
class HideImageForm extends React.PureComponent {
	constructor(props) {
		super(props);
		this.state = {
			imageId: null,
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
					value: this.state.imageId
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
			<HideImageFormView
				l10n={this.props.l10n}
				imageIds={this.props.imageIds}
				handleSubmit={this.handleSubmit}
				hasServerError={this.state.hasServerError}
				handleChange={this.handleChange}
				validationErrors={this.state.validationErrors}
				isDisabled={this.state.isDisabled}
			/>
		);
	}
}

class HideImageFormView extends React.PureComponent {
	render() {
		const {handleSubmit, hasServerError, handleChange, validationErrors = [], isDisabled, l10n = {}, imageIds} = this.props;
		const hasValidationErrors = validationErrors.length > 0;
		return (
			<div className="col-sm-10">
				<form id="hide-image-form" className="form-horizontal" onSubmit={ handleSubmit }>

					{ hasServerError &&
						<div id="hide-image-failed-msg"
							role="alert"
							className="alert alert-danger text-center col-sm-8 col-sm-offset-2">
							{ l10n['t_server_error'] || 'Server error' }
						</div>
					}

					<div className={ `form-group form-group-sm ${hasValidationErrors ? 'has-error' : ''}` }>
						<label htmlFor="hide-image-id" className="control-label col-sm-3 required-field">
							{ l10n['t_image_id'] || 'Image ID' }
						</label>
						<div className="col-sm-4">
							<select
								id="hide-image-id"
								className="form-control"
								required="required"
								onChange={ handleChange }>
								<option value="">{ l10n['t_not_chosen'] || 'Not chosen' }</option>
								{
									this.props.imageIds.map(imageId =>
										<option key={ imageId } value="{ imageId }">{ imageId }</option>
									)
								}
							</select>
							{ hasValidationErrors &&
								<span id="hide-image-id.errors" className="help-block">
									{ validationErrors.join(', ') }
								</span>
							}
						</div>
					</div>

					<div className="form-group from-group-sm">
						<div className="col-sm-offset-3 col-sm-8">
							<button
								type="submit"
								className="btn btn-default btn-sm"
								disabled={ isDisabled }>
								{ l10n['t_hide_image'] || 'Hide image' }
							</button>
						</div>
					</div>
				</form>
			</div>
		);
	}
}

window.HideImageForm = HideImageForm;
