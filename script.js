(function () {
  // immediately executed functions
  var DELIMITER = ',';
  var NEWLINE = '\n';
  var qRegex = /^"|"$/g;
  var input = document.getElementById('file');
  var table = document.getElementById('table');

  if (!input) {
      return;
  }

  input.addEventListener('change', function() {
      if (!!input.files && input.files.length > 0) {
          parseCSV(input.files[0]);
      }
  });

  function parseCSV(file) {
      if (!file || !FileReader) {
          return;
      }

      var reader = new FileReader();

      reader.onload = function (e) {
          toTable(e.target.result);
      };

      reader.readAsText(file);
  }

  function toTable(text) {
      if (!text || !table) {
          return;
      }

      // clear table
      while (!!table.lastElementChild) {
          table.removeChild(table.lastElementChild);
      }

      var rows = text.split(NEWLINE);
      var htr = document.createElement('tr');

      table.appendChild(htr);

      var rtr;

      rows.forEach(function (r) {
          r = r.trim();

          if (!r) {
              return;
          }

          var cols = r.split(DELIMITER);

          if (cols.length === 0) {
              return;
          }

          rtr = document.createElement('tr');

          cols.forEach(function (c) {
              var td = document.createElement('td');
              var tc = c.trim();

              td.textContent = tc.replace(qRegex, '');
              rtr.appendChild(td);
          });

          table.appendChild(rtr);
      })
  }
})();