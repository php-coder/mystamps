//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

// @todo #1280 SimilarSeriesForm: add tests
class SimilarSeriesForm extends React.PureComponent {
	
	constructor(props) {
		super(props);
		this.state = {
			seriesId: props.seriesId,
			similarSeriesId: '',
			isDisabled: false,
			hasServerError: false,
			validationErrors: []
		};
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChange = this.handleChange.bind(this);
	}
	
	handleChange(event) {
		event.preventDefault();
		this.setState({
			similarSeriesId: event.target.value
		});
	}
	
	handleSubmit(event) {
		event.preventDefault();
		
		this.setState({
			isDisabled: true,
			hasServerError: false,
			validationErrors: []
		});
		
		const similarSeriesId = parseInt(this.state.similarSeriesId, 10) || this.state.similarSeriesId;
		
		axios.post(
			this.props.url,
			{
				'seriesId': this.state.seriesId,
				'similarSeriesId': similarSeriesId
			},
			{
				headers: {
					[this.props.csrfHeaderName]: this.props.csrfTokenValue,
					'Cache-Control': 'no-store'
				},
				validateStatus: (status) => {
					return status == 204 || status == 400;
				}
			}
		).then(response => {
			const data = response.data;
			if (data.hasOwnProperty('fieldErrors')) {
				const fieldErrors = [];
				if (data.fieldErrors.seriesId) {
					fieldErrors.push(...data.fieldErrors.seriesId);
				}
				if (data.fieldErrors.similarSeriesId) {
					fieldErrors.push(...data.fieldErrors.similarSeriesId);
				}
				this.setState({ isDisabled: false, validationErrors: fieldErrors });
				return;
			}
			
			// no need to reset the state as page will be reloaded
			window.location.reload();
		
		}).catch(error => {
			console.error(error);
			this.setState({ isDisabled: false, hasServerError: true });
		});
	}
	
	render() {
		return (
			<SimilarSeriesFormView
				l10n={this.props.l10n}
				handleChange={this.handleChange}
				handleSubmit={this.handleSubmit}
				similarSeriesId={this.state.similarSeriesId}
				isDisabled={this.state.isDisabled}
				hasServerError={this.state.hasServerError}
				validationErrors={this.state.validationErrors}
			/>
		)
	}
}

class SimilarSeriesFormView extends React.PureComponent {
	render() {
		const {similarSeriesId, hasServerError, isDisabled, validationErrors, handleChange, handleSubmit} = this.props;
		const hasValidationErrors = validationErrors.length > 0;
		
		return (
			<div className="row">
				<div id="mark-similar-series-failed-msg"
					className={ `alert alert-danger text-center col-sm-8 col-sm-offset-2 ${hasServerError ? '' : 'hidden'}` }>
					{ this.props.l10n['t_server_error'] || 'Server error' }
				</div>
				
				<div className="col-sm-9 col-sm-offset-3">
					<form id="mark-similar-series-form"
						className={`form-inline ${hasValidationErrors ? 'has-error' : ''}`}
						onSubmit={ handleSubmit }>
						
						<div className="form-group form-group-sm">
							<input id="similar-id"
								type="text"
								className="form-control"
								required="required"
								placeholder={ this.props.l10n['t_similar_series_id'] || 'Similar series ID' }
								value={ similarSeriesId }
								onChange={ handleChange }
								disabled={ isDisabled } />
						</div>
						
						<div className="form-group form-group-sm">
							<button type="submit"
								className="btn btn-primary btn-sm"
								disabled={ isDisabled }>
								{ this.props.l10n['t_mark_as_similar'] || 'Mark as similar' }
							</button>
						</div>
						<span id="similar-id.errors" className={`help-block ${hasValidationErrors ? '' : 'hidden'}`}>
							{ validationErrors.join(', ') }
						</span>
						
					</form>
				</div>
			</div>
		)
	}
}
