let updateData = false;

window.onload = function () {


    const url = document.getElementById("url").value;
    const formData = new FormData();
    formData.append("url", url)

    let options = {
        method: "POST",
        body: formData
    }

    fetch(`/firstLoad`, options)
        .then(res => res.json())
        .then(response => {
            response.forEach(element => {
                const mediaFile = element.mediaFile;
                addRow(mediaFile.id, mediaFile.url, mediaFile.downloaded, 0);
            });
            updateData = true;
            updateTable();
        })





    const btnDown = document.getElementById("btnDownload");
    btnDown.addEventListener("click", function () {
        const url = document.getElementById("url").value;
        const formData = new FormData();
        formData.append("url", url)

        let options = {
            method: "POST",
            body: formData
        }

        fetch(`/download`, options)
            .then(res => res.json())
            .then(response => {

                if (response.respuesta == true) {
                    updateData = true;
                    updateTable();
                    const mediaFile = response.mediaFile;
                    addRow(mediaFile.id, mediaFile.url, mediaFile.downloaded, 0);
                    updateData = true;
                }


            })
    });


    const updateTable = async function updateTable() {
        while (updateData) {
            let options = {
                method: "POST",
            }

            fetch(`/getInfo`, options)
                .then(res => res.json())
                .then(response => {
                    if (response == "") {
                        updateData = false;
                    }

                    response.forEach(element => {
                        const porcentaje = element.porcentaje;
                        const mediaFile = element.mediaFile;
                        updateBarProgress(mediaFile.id, mediaFile.downloaded, porcentaje)
                    });

                })
            await new Promise(resolve => setTimeout(resolve, 300));
        }
        const tbody2 = document.getElementById("tbodyContent");
        tbody2.innerHTML = "";
    };

    updateTable();
};



// Función para añadir una fila a la tabla
function addRow(id, url, downloaded, porcentaje) {
    // Obtener la tabla
    const table = document.getElementById('mediaTable').getElementsByTagName('tbody')[0];

    // Crear una nueva fila
    const newRow = table.insertRow();
    newRow.id = "row" + id;

    // Crear celdas en la nueva fila
    const cell1 = newRow.insertCell(0); // Columna ID
    const cell2 = newRow.insertCell(1); // Columna URL
    const cell3 = newRow.insertCell(2); // Columna %

    // Asignar los valores a las celdas
    cell1.textContent = id; // ID auto incremental
    cell2.innerHTML = `
                <div class="progress-container">
                    <div class="progress-bar" id="progressBar${id}"></div>
                </div>
                <div class="url-text">${url}</div>
            `;
    cell3.id = "porcenShell" + id;
    cell3.textContent = `${porcentaje}%`; // Porcentaje aleatorio
}

function updateBarProgress(id, estado, porcentaje) {
    const progressBar = document.getElementById("progressBar" + id);
    const porcenShell = document.getElementById("porcenShell" + id);
    const downloadRow = document.getElementById("row" + id);
    // if (estado = true) {
    //     progressBar.style.width = 100 + '%';
    //     porcenShell.innerHTML = 100 + '%';
    //     // downloadRow.style.backgroundColor = "green";
    // }
    console.log("PORCENTAJE -----> " + porcentaje)
    porcenShell.innerHTML = porcentaje;
    if (porcentaje == "Recoding") {
        console.log("RECODDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD")
        progressBar.style.width = 100 + '%';
    } else {
        progressBar.style.width = porcentaje + '%';
    }

}

