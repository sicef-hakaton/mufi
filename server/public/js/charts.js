google.load("visualization", "1", {packages:["corechart"]});
google.setOnLoadCallback(initGoogle);

var line_max_data_points = 50;
	  
var line_data;
var line_options;
var line_chart;

//asdasd
var last_val = 50;

function initGoogle() {
	initLineChart();
}

function initLineChart() {
	line_data = new google.visualization.DataTable();
	line_data.addColumn('datetime', 'Time');
	line_data.addColumn('number', 'Understanding');
	line_options = {
		title: 'Understanding over time',
		//curveType: 'function',
		vAxis: {
			minValue:0, 
			maxValue:100
		},
		animation: {
			duration: 1000,
			easing: 'in'
		},
		legend: {
			position: 'none'
		}
	}
	line_chart = new google.visualization.LineChart(document.getElementById('line_chart_div'));
	drawLineChart();
	
};

function nextVal() {
	last_val = last_val + Math.floor((Math.random() * 21)) - 10;
	return last_val;
}
function drawLineChart() {
	line_chart.draw(line_data, line_options);
}

function updateLineData(date, value) {
	var new_row = [date, value];
	
	line_data.addRow(new_row);
	
	var num_rows = line_data.getNumberOfRows();
	if (num_rows > line_max_data_points) {
		var to_remove = num_rows - line_max_data_points;
		line_data.removeRows(0, to_remove);
	}
	drawLineChart();
}

var column_number = 4;
var column_raw_data = [0, 0, 0, 0];
var column_prefix = ['A', 'B', 'C', 'D'];

var column_data;
var column_options;
var column_chart;

function initColumn(number) {
	column_number = number;
	column_prefix = ['A', 'B', 'C', 'D'];
	column_prefix = column_prefix.slice(0, column_number);
	column_raw_data = [0, 0, 0, 0];
	column_raw_data = column_raw_data.slice(0, column_number);
	// Init data, options and chart
	column_data = new google.visualization.DataTable();
	column_options = {
		title: 'Answer Distribution',
		animation:{
			duration: 500,
			easing: 'out',
		},
		vAxis: {minValue:0, maxValue:20}
	};
	column_chart = new google.visualization.ColumnChart(document.getElementById('col_chart_div'));
	
	// Setup table structure
	// Add columns
	column_data.addColumn('string', 'Answer');
	column_prefix.forEach(function (value) {
		column_data.addColumn('number', value);
	});
	// Prepare the row
	render_data = column_raw_data;
	render_data.unshift('Answers');
	// Add row
	column_data.addRow(render_data);
	
	// Draw the initial empty chart
	drawColumnChart();			
}

function drawColumnChart() {				
	column_chart.draw(column_data, column_options);
}

function setColumnDataPoint(value, index) {
	column_data.setValue(0, index + 1, value);
	drawColumnChart();
}

function updateColumnDataPoint(delta, index) {
	var r = 0;
	var c = index+1;
	var value = column_data.getValue(r, c) + delta;
	column_data.setValue(r, c, value);
	drawColumnChart();
}

function setAllColumnData(values) {			
	values.forEach(function(value, index) {
		column_data.setValue(0, index + 1, value);
	});
	drawColumnChart();
}



