//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

// @todo #1341 AddCatalogNumbersForm: add tests
class AddCatalogNumbersForm extends React.PureComponent {
	constructor(props) {
		super(props);
		this.state = {
			numbers: null,
			catalog: 'michel',
			hasServerError: false,
			validationErrors: [],
			isDisabled: false
		};
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChangeNumbers = this.handleChangeNumbers.bind(this);
		this.handleChangeCatalog = this.handleChangeCatalog.bind(this);
	}

	handleChangeNumbers(event) {
		event.preventDefault();
		this.setState({
			numbers: event.target.value
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
						path: `/${this.state.catalog}_numbers`,
						value: this.state.numbers
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
			<AddCatalogNumbersFormView
				l10n={this.props.l10n}
				handleSubmit={this.handleSubmit}
				hasServerError={this.state.hasServerError}
				handleChangeCatalog={this.handleChangeCatalog}
				handleChangeNumbers={this.handleChangeNumbers}
				validationErrors={this.state.validationErrors}
				isDisabled={this.state.isDisabled}
			/>
		);
	}
}

class AddCatalogNumbersFormView extends React.PureComponent {
	render() {
		const {handleSubmit, hasServerError, handleChangeCatalog, handleChangeNumbers, validationErrors, isDisabled, l10n} = this.props;
		const hasValidationErrors = validationErrors.length > 0;

		return (
			<div className="col-sm-12 form-group">
				<form id="add-catalog-numbers-form"
					className={ `form-horizontal ${hasValidationErrors ? 'has-error' : ''}` }
					onSubmit={ handleSubmit }>
					<div
						id="add-catalog-numbers-failed-msg"
						className={ `alert alert-danger text-center col-sm-8 col-sm-offset-2 ${hasServerError ? '' : 'hidden'}` }>
						{ l10n['t_server_error'] || 'Server error' }
					</div>
					<div className="form-group form-group-sm">
						<label htmlFor="catalog-name" className="control-label col-sm-3">
							{ l10n['t_catalog'] || 'Catalog' }
						</label>
						<div className="col-sm-6">
							<select
								id="catalog-name"
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
						<label htmlFor="catalog-numbers" className="control-label col-sm-3 required-field">
							{ l10n['t_numbers'] || 'Numbers' }
						</label>
						<div className="row">
							<div className="col-sm-6">
								<input
									id="catalog-numbers"
									type="text"
									className="form-control"
									size="5"
									required="required"
									placeholder={ `${l10n['t_example'] || 'Example'}: 90-92,117` }
									onChange={ handleChangeNumbers } />
							</div>
						</div>
					</div>
					<div className="col-sm-offset-3 col-sm-4">
						<span
							id="catalog-numbers.errors"
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

window.AddCatalogNumbersForm = AddCatalogNumbersForm;
