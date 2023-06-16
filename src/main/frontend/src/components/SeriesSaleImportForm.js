//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

// @todo #1057 SeriesSaleImportForm: add tests
class SeriesSaleImportForm extends React.PureComponent {
	
	constructor(props) {
		super(props);
		this.state = {
			url: '',
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
			url: event.target.value
		});
	}
	
	handleSubmit(event) {
		event.preventDefault();
		
		/*
		 * @todo #1057 SeriesSaleImportForm: wait until setState() finishes
		 */
		// (see https://reactjs.org/docs/react-component.html#setstate)
		this.setState({
			isDisabled: true,
			hasServerError: false,
			validationErrors: []
		});
		
		axios.post(
			this.props.url,
			{
				'url': this.state.url
			},
			{
				headers: {
					[this.props.csrfHeaderName]: this.props.csrfTokenValue,
					'Cache-Control': 'no-store'
				},
				validateStatus: (status) => {
					return status == 200 || status == 400;
				}
			}
		).then(response => {
			const data = response.data;
			if (data.hasOwnProperty('fieldErrors')) {
				this.setState({ isDisabled: false, validationErrors: data.fieldErrors.url });
				return;
			}
			
			const today = DateUtils.formatDateToDdMmYyyy(new Date());
			document.getElementById('date').value = today;
			document.getElementById('url').value = this.state.url;
			
			if (data.price) {
				document.getElementById('price').value = data.price;
			}
			
			if (data.currency) {
				document.getElementById('currency').value = data.currency;
			}
			
			if (data.altPrice) {
				document.getElementById('alt-price').value = data.altPrice;
			}
			
			if (data.altCurrency) {
				document.getElementById('alt-currency').value = data.altCurrency;
			}
			
			if (data.sellerId) {
				document.getElementById('seller').value = data.sellerId;
			}
			
			if (data.condition) {
				document.getElementById('condition').value = data.condition;
			}
			
			this.setState({ isDisabled: false, url: '' });
		
		}).catch(error => {
			console.error(error);
			this.setState({ isDisabled: false, hasServerError: true });
		});
	}
	
	render() {
		return (
			<SeriesSaleImportFormView
				l10n={this.props.l10n}
				hasServerError={this.state.hasServerError}
				handleSubmit={this.handleSubmit}
				url={this.state.url}
				handleChange={this.handleChange}
				isDisabled={this.state.isDisabled}
				validationErrors={this.state.validationErrors}
			/>
		)
	}
}

class SeriesSaleImportFormView extends React.PureComponent {
	render() {
		const { hasServerError, handleSubmit, url, handleChange, isDisabled, validationErrors, l10n} = this.props;
		const hasValidationErrors = validationErrors.length > 0;
		
		return (
			<div className="row">
				<div className="col-sm-12">
					<div className="row">
						<div className="col-sm-12">
							<h5>
								{ l10n['t_import_info_who_selling_series'] || 'Import info about selling this series' }
							</h5>
						</div>
					</div>
					<div className="row">
						<div id="import-series-sale-failed-msg"
							className={ `alert alert-danger text-center col-sm-8 col-sm-offset-2 ${hasServerError ? '' : 'hidden'}` }>
							{ l10n['t_could_not_import_info'] || 'Could not import information from this page' }
						</div>
					</div>
					<div className="row">
						<div className="col-sm-12">
							<form id="import-series-sale-form"
								className={ `form-horizontal ${hasValidationErrors ? 'has-error' : ''}` }
								onSubmit={ handleSubmit }>
								
								<div className="form-group form-group-sm">
									<label htmlFor="series-sale-url" className="control-label col-sm-3 required-field">
										{ l10n['t_url'] || 'URL' }
									</label>
									<div className="col-sm-6">
										<input id="series-sale-url"
											name="url"
											type="url"
											className="form-control"
											required="required"
											value={ url }
											onChange={ handleChange }
											disabled={ isDisabled } />
										<span id="series-sale-url.errors"
											className={ `help-block ${hasValidationErrors ? '' : 'hidden'}` }>
											{ validationErrors.join(', ') }
										</span>
									</div>
								</div>
								
								<div className="form-group form-group-sm">
									<div className="col-sm-offset-3 col-sm-4">
										<button type="submit"
											className="btn btn-primary btn-sm"
											disabled={ isDisabled }>
											{ l10n['t_import_info'] || 'Import info' }
										</button>
									</div>
								</div>
								
							</form>
						</div>
					</div>
				</div>
			</div>
		)
	}
}

window.SeriesSaleImportForm = SeriesSaleImportForm;
