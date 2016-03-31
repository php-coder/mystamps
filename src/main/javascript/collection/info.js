function initPage(statByCategories, statByCountries) {
	var chartsVersion = '44';
	google.charts.load(chartsVersion, {'packages':['corechart']});
	google.charts.setOnLoadCallback(function drawCharts() {
		drawChart('categories-chart', createCategoriesDataTable(statByCategories));
		drawChart('countries-chart', createCountriesDataTable(statByCountries));
	});
}

function drawChart(containerId, dataTable) {
	var options = {
		pieHole: 0.3
	};
	var chart = new google.visualization.PieChart(document.getElementById(containerId));
	chart.draw(dataTable, options);
};

function createCategoriesDataTable(stat) {
	var table = new google.visualization.DataTable();
	table.addColumn('string', 'Category');
	table.addColumn('number', 'Quantity of stamps');
	table.addRows(stat);
	return table;
};

function createCountriesDataTable(stat) {
	var table = new google.visualization.DataTable();
	table.addColumn('string', 'Country');
	table.addColumn('number', 'Quantity of stamps');
	table.addRows(stat);
	return table;
};
