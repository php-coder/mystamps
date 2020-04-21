class AddCountryForm extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {
			nameInEnglish: "",
			nameInRussian: ""
		};
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChange = this.handleChange.bind(this);
	}
	
	handleChange(event) {
		event.preventDefault();		
		this.setState({
			[event.target.name]: event.target.value
		});
	}
	
	handleSubmit(event) {
		event.preventDefault();

		this.setState({});
		
		axios.post(
			"categories",{},{}
		).then(response => {

		}).catch(error => {

		});
		
	}
	
	render() {
		return (
			<form id="add-country-form" className="form-horizontal" onSubmit={this.handleSubmit}
					/* method="post" action="info.html" th:action="@{${ADD_COUNTRY_PAGE}}" th:object="${addCountryForm}" */>
				
				<div className="form-group" /* th:classappend="${#fields.hasErrors('name') ? 'has-error' : ''}" */>
					<label htmlFor="name" className="control-label col-sm-4">
						<span /* th:remove="tag" th:text="#{t_name_in_english}" */>
							Name (in English)
						</span>
						<span className="required_field">*</span>
					</label>
					<div className="col-sm-5">
						<input id="name"
							type="text"
							className="form-control"
							value={this.state.nameInEnglish}
							name="nameInEnglish"
							required="required"
							onChange={this.handleChange} /* th:field="*{name}" */ />
						{/*
						<span id="name.errors" class="help-block" th:if="${#fields.hasErrors('name')}" th:each="error : ${#fields.errors('name')}" th:text="${error}"></span>
						 */}
					</div>
				</div>
				
				<div className="form-group" /* th:classappend="${#fields.hasErrors('nameRu') ? 'has-error' : ''}" */>
					<label htmlFor="nameRu" className="control-label col-sm-4" /* th:text="#{t_name_in_russian}" */>
						Name (in Russian)
					</label>
					<div className="col-sm-5">
						<input id="nameRu"
							type="text" 
							className="form-control"
							value={this.state.nameInRussian}
							name="nameInRussian"
							onChange={this.handleChange} /* th:field="*{nameRu}" */ />
						{/* 
						<span id="nameRu.errors" class="help-block" th:if="${#fields.hasErrors('nameRu')}" th:each="error : ${#fields.errors('nameRu')}" th:text="${error}"></span>
						 */}
					</div>
				</div>
				
				<div className="form-group">
					<div className="col-sm-offset-4 col-sm-5">
						<input type="submit"
							className="btn btn-primary"
							value="Add" /* th:value="#{t_add}" */ />
					</div>
				</div>
				
			</form>
		)
	}
}
