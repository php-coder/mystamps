function initPage(statByCategories, statByCountries) {
	google.charts.load('44', {'packages':['corechart']});
	google.charts.setOnLoadCallback(function() {
		drawChart('categories-chart', createCategoriesDataTable(statByCategories));
		drawChart('countries-chart', createCountriesDataTable(statByCountries));
	});
}

function drawChart(containerId, table) {
	var options = {
		pieHole: 0.3
	};
	var chart = new google.visualization.PieChart(document.getElementById(containerId));
	chart.draw(table, options);
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
