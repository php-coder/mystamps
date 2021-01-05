//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

// @todo #1344 AddReleaseYearForm: add tests
class AddReleaseYearForm extends React.PureComponent {
	constructor(props) {
		super(props);
		this.state = {
			year: null,
			hasServerError: false,
			validationErrors: [],
			isDisabled: false,
		};
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChange = this.handleChange.bind(this);
	}

	handleChange(event) {
		event.preventDefault();
		this.setState({
			year: event.target.value
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
						path: '/release_year',
						value: this.state.year
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
			<AddReleaseYearFormView
				l10n={this.props.l10n}
				handleSubmit={this.handleSubmit}
				hasServerError={this.state.hasServerError}
				handleChange={this.handleChange}
				validationErrors={this.state.validationErrors}
				isDisabled={this.state.isDisabled}
				sinceYear={this.props.sinceYear}
				tillYear={this.props.tillYear}
			/>
		);
	}
}

class AddReleaseYearFormView extends React.PureComponent {

	generateRange(start, end) {
		return new Array(end - start + 1)
			.fill()
			.map((_, idx) => start + idx);
	}

	render() {
		const {handleSubmit, hasServerError, handleChange, validationErrors, isDisabled, sinceYear, tillYear, l10n} = this.props;
		const hasValidationErrors = validationErrors.length > 0;
		const rangeOfYears = this.generateRange(sinceYear, tillYear);
		
		return (
			<React.Fragment>
				<div className="row">
					<div id="add-release-year-failed-msg"
						className={ `alert alert-danger text-center col-sm-6 col-sm-offset-3 ${hasServerError ? '' : 'hidden'}` }>
						{ l10n['t_server_error'] || 'Server error' }
					</div>
				</div>
				<div className="row">
					<div className="col-sm-9 col-sm-offset-3 form-group">
						<form id="add-release-year-form"
							className={ `form-inline ${hasValidationErrors ? 'has-error' : ''}` }
							onSubmit={ handleSubmit }>
							<div className="form-group form-group-sm">
								<select
									id="release-year"
									name="release-year"
									className="form-control"
									required="required"
									onChange={ handleChange }>
									<option value="">{ l10n['t_year'] || 'Year' }</option>
									{rangeOfYears.map(year => (
										<option key={year.toString()} value={year}>{ year }</option>
									))}
								</select>
							</div>
							<div className="form-group form-group-sm">
								<button type="submit"
									className="btn btn-primary btn-sm"
									disabled={ isDisabled }>
									{ l10n['t_add'] || 'Add' }
								</button>
							</div>
							<span
								id="release-year.errors"
								className={ `help-block ${hasValidationErrors ? '' : 'hidden'}` }>
								{ validationErrors.join(', ') }
							</span>
						</form>
					</div>
				</div>
			</React.Fragment>
		);
	}
}

window.AddReleaseYearForm = AddReleaseYearForm;
