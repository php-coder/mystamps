//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

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
					if (data.fieldErrors.comment) {
						fieldErrors.push(...data.fieldErrors.comment);
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
		const {handleSubmit, hasServerError, handleChange, validationErrors, isDisabled} = this.props;
		const hasValidationErrors = validationErrors.length > 0;
		return (
			<div className="col-sm-12 form-group">
				<form className={ `form-horizontal ${hasValidationErrors ? 'has-error' : ''}` } onSubmit={ handleSubmit }>
					<div
						id="add-comment-failed-msg"
						className={ `alert alert-danger text-center col-sm-8 col-sm-offset-2 ${hasServerError ? '' : 'hidden'}` }>
						{ this.props.l10n['t_server_error'] || 'Server error' }
					</div>
					<div className="form-group form-group-sm">
						<label className="control-label col-sm-3">
							{ this.props.l10n['t_comment'] || 'Comment' }
						</label>
						<div className="col-sm-6">
							<textarea
								id="comment"
								className="form-control"
								cols="22"
								rows="3"
								required="required"
								onChange={ handleChange }>
							</textarea>
						</div>
					</div>
					<div className="col-sm-offset-3 col-sm-4">
						<span
							id="comment.errors"
							className={`help-block ${hasValidationErrors ? '' : 'hidden'}`}>
							{ validationErrors.join(', ') }
						</span>
						<button
							type="submit"
							className="btn btn-primary btn-sm"
							disabled={ isDisabled }>
							{ this.props.l10n['t_add'] || 'Add' }
						</button>
					</div>
				</form>
			</div>
		);
	}
}
