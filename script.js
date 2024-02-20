const csvFile = document.getElementById("game_history.csv");


function fetchCSVFile() {
    const selCSVFile = csvFile.files[0];
    const readCSV = new FileReader();
    readCSV.onload = function (e) {
        const data = e.target.result;
        document.write(data);
    };
    readCSV.readAsText(selCSVFile);
};

<script type="text/javascript">
    function fetchCSVFile(){
    var csvSel = document.querySelector('#csvFile').files;
    if(csvSel.length > 0 ){
   // Selecting CSV file residing at first index
    var provFile = csvSel[0];
    var readCsv = new FileReader();
    // Method to Read file as String
    readCsv.readAsText(provFile);
    // invokes when file is read successfully
    readCsv.onload = function(e) {
   // Reading the provided CSV file data
    var csvFileData = e.target.result;
    // Generating rows Array of the data by splitting through line breaks
    var tableRow = csvFileData.split('\n')
</script>