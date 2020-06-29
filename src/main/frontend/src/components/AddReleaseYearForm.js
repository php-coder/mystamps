//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

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
			<div className="row">
				<div id="add-release-year-failed-msg"
					className={ `alert alert-danger text-center col-sm-8 col-sm-offset-2 ${hasServerError ? '' : 'hidden'}` }>
					{ l10n['t_server_error'] || 'Server error' }
				</div>
				<div className="col-sm-12">
					<form id="add-release-year-form"
						className={`form-horizontal ${hasValidationErrors ? 'has-error' : ''}`}
						onSubmit={ handleSubmit }>
						<label htmlFor="release-year" className="control-label col-sm-3">
							{ l10n['t_year'] || 'Year' }
						</label>
						<div className="form-group form-group-sm col-sm-5">
							<select
								id="release-year"
								name="release-year"
								className="form-control"
								required="required"
								onChange={ handleChange }>
								<option value=""></option>
								{rangeOfYears.map(year => (
									<option key={year.toString()} value={year}>{ year }</option>
								))}
							</select>
							<span
								id="release-year.errors"
								className={ `help-block ${hasValidationErrors ? '' : 'hidden'}` }>
								{ validationErrors.join(', ') }
							</span>
						</div>
						<div className="form-group form-group-sm">
							<button type="submit"
								className="btn btn-primary btn-sm"
								disabled={ isDisabled }>
								{ l10n['t_add'] || 'Add' }
							</button>
							
						</div>
						
					</form>
				</div>
			</div>
		);
	}
}

window.AddReleaseYearForm = AddReleaseYearForm;
