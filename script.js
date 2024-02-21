(function () {
  document.addEventListener("DOMContentLoaded", function () {
    // Fetch the CSV file
    fetch("game_history.csv")
      .then((response) => response.text())
      .then((data) => {
        // Parse CSV data
        const rows = data.split("\n");
        const table = document.getElementById("table");

        // Create a table and append it to the 'table' div
        const htmlTable = document.createElement("table");
        rows.forEach((rowData) => {
          if (!rowData) {
            // don't add empty rows
            return;
          }
          const row = document.createElement("tr");
          const columns = rowData.split(",");

          columns.forEach((columnData) => {
            // Exclude empty values
            if (columnData.trim() !== "") {
              const column = document.createElement("td");

              // Apply CSS class based on values
              column.className = getColumnColorClass(columnData.trim().toUpperCase());

              column.textContent = columnData;
              row.appendChild(column);
            }
          });

          htmlTable.appendChild(row);
        });

        table.appendChild(htmlTable);
      })
      .catch((error) => console.error("Error fetching CSV:", error));
  });

  function getColumnColorClass(value) {
    switch (value) {
      case "B":
        return "blue";
      case "P":
        return "red";
      case "D":
        return "green";
      default:
        return "";
    }
  }
})();
