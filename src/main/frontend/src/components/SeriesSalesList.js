//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

class SeriesSalesList extends React.PureComponent {
	
	constructor(props) {
		super(props);
		this.state = {
			sales: [],
			hasServerError: false,
		};
		this.loadSales = this.loadSales.bind(this);
	}

	componentDidMount() {
		this.loadSales();
	}

	loadSales() {
		this.setState({
			hasServerError: false,
			sales: []
		});
		
		axios.get(this.props.url)
			.then(response => {
				const data = response.data;
				this.setState({ sales: data });
		
			})
			.catch(error => {
				console.error(error);
				this.setState({ hasServerError: true });
			});
	}
	
	render() {
		return (
			<SeriesSalesListView
				l10n={this.props.l10n}
				hasServerError={this.state.hasServerError}
				sales={this.state.sales}
			/>
		)
	}
}

class SeriesSalesListView extends React.PureComponent {
	render() {
		const { hasServerError, l10n, sales } = this.props;

		return (
			<div className="row">
				<div className="col-sm-12">
					<h5>{ l10n['t_who_selling_series'] || 'Who was selling/buying this series' }</h5>
					 <div className="row">
						<div id="loading-series-sales-failed-msg"
							className={ `alert alert-danger text-center col-sm-8 col-sm-offset-2 ${hasServerError ? '' : 'hidden'}` }>
							{ l10n['t_server_error'] || 'Server error' }
						</div>
					</div>
					<ul>
						{ sales.map((sale, index) => (
							<SeriesSaleItem
								key={sale.id}
								l10n={this.props.l10n}
								sale={sale}
								index={index + 1}
							/>
						))}
					</ul>
				</div>
			</div>
		)
	}
}

class SeriesSaleItem extends React.PureComponent {
	render() {
		const { sale, index, l10n } = this.props;
		const hasBuyer = !!sale.buyerName;
		const hasCondition = !!sale.condition;
		const hasDate = !!sale.date;
		const hasTransactionUrl = !!sale.transactionUrl;
		const hasSecondPrice = !!sale.secondPrice;

		return (
			<li id={ `series-sale-${index}-info` }>
				{ hasDate && sale.date }
				{' '}
				<ParticipantLink url={sale.sellerUrl} name={sale.sellerName} />
				{' '}
				{ hasBuyer ?
					(l10n['t_sold_to'] || 'sold to')
					: (l10n['t_was_selling'] || 'was selling for')
				}
				{' '}
				{ hasBuyer && (<ParticipantLink url={sale.buyerUrl} name={sale.buyerName} />) }
				{' '}
				{ hasBuyer && (l10n['t_sold_for'] || 'for') }
				{' '}
				{ hasTransactionUrl ?
					<a href={sale.transactionUrl} id={ `series-sale-${index}-transaction` } rel="nofollow">
						{ `${sale.firstPrice}\u00A0${sale.firstCurrency}` }
						{ hasSecondPrice && `(${sale.secondPrice}\u00A0${sale.secondCurrency})` }
					</a>
					: <React.Fragment>
						{ `${sale.firstPrice}\u00A0${sale.firstCurrency}` }
						{ hasSecondPrice && `(${sale.secondPrice}\u00A0${sale.secondCurrency})` }
					</React.Fragment>
				}
				{' '}
				{ hasCondition && (sale.condition !== 'CANCELLED' ? sale.condition : (l10n['t_cancelled'] || 'cancelled')) }
			</li>
		)
	}
}

class ParticipantLink extends React.PureComponent {
	render() {
		const { name, url } = this.props;
		const hasUrl = !!url;
		return (
			hasUrl ?
				<a href={url} rel="nofollow">{ name }</a>
				: name
		)
	}
}

window.SeriesSalesList = SeriesSalesList;
