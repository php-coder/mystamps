//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

// @todo #1057 SeriesSaleImportForm: add tests
class SeriesSaleImportForm extends React.Component {
	
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
		
		if (this.state.url == '') {
			return;
		}
		
		// @todo #1057 SeriesSaleImportForm: wait until setState() finishes
		// (see https://reactjs.org/docs/react-component.html#setstate)
		this.setState({
			isDisabled: true,
			hasServerError: false,
			validationErrors: []
		});
		
		const headers = new Headers();
		headers.set('Content-Type', 'application/json; charset=UTF-8');
		headers.set(this.props.csrfHeaderName, this.props.csrfTokenValue);
		
		const request = new Request(
			this.props.url,
			{
				method: 'POST',
				headers,
				body: JSON.stringify({ 'url': this.state.url }),
				cache: 'no-store'
			}
		);
		
		fetch(request)
			.then(response => {
				if (response.ok || response.status == 400) {
					return response.json();
				}
				throw new Error(response.statusText);
			})
			.then(data => {
				if (data.hasOwnProperty('fieldErrors')) {
					this.setState({
						isDisabled: false,
						validationErrors: data.fieldErrors.url
					});
					return;
				}
				
				const today = DateUtils.formatDateToDdMmYyyy(new Date());
				document.getElementById('date').value = today;
                
				document.getElementById('url').value = this.state.url;
				document.getElementById('price').value = data.price;
				document.getElementById('currency').value = data.currency;
				if (data.sellerId) {
					document.getElementById('seller').value = data.sellerId;
				}
				this.setState({ isDisabled: false, url: '' });
			})
			.catch(error => {
				console.error(error);
				this.setState({
					isDisabled: false,
					hasServerError: true
				});
			});
	}
	
	render() {
		const hasValidationErrors = this.state.validationErrors.length > 0;
		
		return (
			<div className="row">
				<div className="col-sm-12">
					<div className="row">
						<div className="col-sm-12">
							<h5>
								{ this.props.l10n['t_import_info_who_selling_series'] || 'Import info about selling this series' }
							</h5>
						</div>
					</div>
					<div className="row">
						<div id="import-series-sale-failed-msg"
							className={`alert alert-danger text-center col-sm-8 col-sm-offset-2 ${this.state.hasServerError ? '' : 'hidden'}`}>
							{ this.props.l10n['t_could_not_import_info'] || 'Could not import information from this page' }
						</div>
					</div>
					<div className="row">
						<div className="col-sm-12">
							<form id="import-series-sale-form"
								className={`form-horizontal ${hasValidationErrors ? 'has-error' : ''}`}
								onSubmit={this.handleSubmit}>
								
								<div className="form-group form-group-sm">
									<label htmlFor="series-sale-url" className="control-label col-sm-3">
										{ this.props.l10n['t_url'] || 'URL' }
										<span className="required_field"> *</span>
									</label>
									<div className="col-sm-6">
										<input id="series-sale-url"
											name="url"
											type="url"
											className="form-control"
											required="required"
											value={this.state.url}
											onChange={this.handleChange}
											disabled={this.state.isDisabled} />
										<span id="series-sale-url.errors"
											className={`help-block ${hasValidationErrors ? '' : 'hidden'}`}>
											{ this.state.validationErrors.join(', ') }
										</span>
									</div>
								</div>
								
								<div className="form-group form-group-sm">
									<div className="col-sm-offset-3 col-sm-4">
										<button type="submit"
											className="btn btn-primary"
											disabled={this.state.isDisabled}>
											{ this.props.l10n['t_import_info'] || 'Import info' }
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
