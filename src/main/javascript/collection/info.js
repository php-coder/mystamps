//
// IMPORTANT:
// You must update Url.RESOURCES_VERSION each time whenever you're modified this file!
//

function initPage(statByCategories, statByCountries) {
	var chartsVersion = '44';
	google.charts.load(chartsVersion, {'packages':['corechart']});
	google.charts.setOnLoadCallback(function drawCharts() {
		drawChart('categories-chart', createDataTable(statByCategories));
		drawChart('countries-chart', createDataTable(statByCountries));
	});
}

function drawChart(containerId, dataTable) {
	var options = {
		pieHole: 0.3
	};
	var chart = new google.visualization.PieChart(document.getElementById(containerId));
	chart.draw(dataTable, options);
}

function createDataTable(stat) {
	var table = new google.visualization.DataTable();
	table.addColumn('string', 'Category/Country');
	table.addColumn('number', 'Quantity of stamps');
	table.addRows(stat);
	return table;
}
