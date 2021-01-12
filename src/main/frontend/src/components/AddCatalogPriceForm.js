//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//
// @todo #1342 AddCatalogPriceForm: add tests
// @todo #1388 AddCatalogPriceForm: consider using a tooltip for currency

class AddCatalogPriceForm extends React.PureComponent {
	constructor(props) {
		super(props);
		this.state = {
			price: null,
			catalog: 'michel',
			hasServerError: false,
			validationErrors: [],
			isDisabled: false
		};
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChangePrice = this.handleChangePrice.bind(this);
		this.handleChangeCatalog = this.handleChangeCatalog.bind(this);
	}

	handleChangePrice(event) {
		event.preventDefault();
		this.setState({
			price: event.target.value
		});
	}

	handleChangeCatalog(event) {
		event.preventDefault();
		this.setState({
			catalog: event.target.value
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
				this.props.url,
				[
					{
						op: 'add',
						path: `/${this.state.catalog}_price`,
						value: this.state.price
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
				}
			)
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
			<AddCatalogPriceFormView
				l10n={this.props.l10n}
				handleSubmit={this.handleSubmit}
				hasServerError={this.state.hasServerError}
				handleChangeCatalog={this.handleChangeCatalog}
				catalog={this.state.catalog}
				handleChangePrice={this.handleChangePrice}
				validationErrors={this.state.validationErrors}
				isDisabled={this.state.isDisabled}
			/>
		);
	}
}

class AddCatalogPriceFormView extends React.PureComponent {

	getCurrencyByCatalogName(catalog) {
		switch (catalog) {
			case 'michel':
			case 'yvert':
				return ['\u20AC', 'EUR'];
			case 'scott':
				return ['$', 'USD'];
			case 'gibbons':
				return ['\u00A3', 'GBP'];
			case 'solovyov':
			case 'zagorski':
				return ['\u20BD', 'RUB'];
		}
	}
	render() {
		const {handleSubmit, hasServerError, handleChangeCatalog, handleChangePrice, validationErrors, isDisabled, catalog, l10n} = this.props;
		const hasValidationErrors = validationErrors.length > 0;
		const [currencySymbol, currencyName] = this.getCurrencyByCatalogName(catalog);
		return (
			<div className="col-sm-12 form-group">
				<form id="add-catalog-price-form" className={ `form-horizontal ${hasValidationErrors ? 'has-error' : ''}` }
					onSubmit={ handleSubmit }>
					<div
						id="add-catalog-price-failed-msg"
						className={ `alert alert-danger text-center col-sm-8 col-sm-offset-2 ${hasServerError ? '' : 'hidden'}` }>
						{ l10n['t_server_error'] || 'Server error' }
					</div>
					<div className="form-group form-group-sm">
						<label htmlFor="price-catalog-name" className="control-label col-sm-3">
							{ l10n['t_catalog'] || 'Catalog' }
						</label>
						<div className="col-sm-6">
							<select
								id="price-catalog-name"
								name="catalogName"
								className="form-control"
								onChange={ handleChangeCatalog }>
								<option value="michel">{ l10n['t_michel'] || 'Michel' }</option>
								<option value="scott">{ l10n['t_scott'] || 'Scott' }</option>
								<option value="yvert">{ l10n['t_yvert'] || 'Yvert et Tellier' }</option>
								<option value="gibbons">{ l10n['t_sg'] || 'Stanley Gibbons' }</option>
								<option value="solovyov">{ l10n['t_solovyov'] || 'Solovyov' }</option>
								<option value="zagorski">{ l10n['t_zagorski'] || 'Zagorski' }</option>
							</select>
						</div>
					</div>
					<div className="form-group form-group-sm">
						<label htmlFor="catalog-price" className="control-label col-sm-3 required-field">
							{ l10n['t_price'] || 'Price' }
						</label>
						<div className="col-sm-3">
							<div className="input-group">
								<span className="input-group-addon">{ currencySymbol }</span>
								<input
									id="catalog-price"
									type="text"
									className="form-control"
									size="5"
									title={ currencyName }
									required="required"
									onChange={ handleChangePrice } />
							</div>
						</div>
					</div>
					<div className="col-sm-offset-3 col-sm-4">
						<span
							id="catalog-price.errors"
							className={ `help-block ${hasValidationErrors ? '' : 'hidden'}` }>
							{ validationErrors.join(', ') }
						</span>
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

window.AddCatalogPriceForm = AddCatalogPriceForm;
