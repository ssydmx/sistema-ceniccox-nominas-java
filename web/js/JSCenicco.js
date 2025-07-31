function handleDataTableHide(args) {
    if (args.isValidate) {
        searchDialog.hide();
    }
    if (args.errores) {
        searchDialog.hide();
        errorDialog.show();
    }
}

function handlelShowRecibos(args) {
    if (args.isValidate) {
        location.href = args.ruta;
    } else {
//        jhandleLoginRequestQuery('#formLogin').effect("shake", {times: 3}, 100);
    }
}
function  handleCreateInformacionVacaciones(args) {
    if (args.isValidate) {
        createDialog.hide();
        informacionDialog.show();
    }
}
function  handleCreateSolicitudVacaciones(args) {
    if (args.isValidate) {
        loadDialogVacaciones.hide();
        informacionBitacoraDialog.show();

    }
}
function  handleCreateAdelantoVacaciones(args) {
    if (args.isValidate) {
        informacionDialog.hide();
    }
}
function  handleCreateInicidencia(args) {
    if (args.isValidate) {
        createDialog.show();
    }
}
function  handleCreateInicidenciaArchivo(args) {
    if (args.isValidate) {
        incidenciasDialog.show();
    }
}
function  handleCloseInicidenciaArchivo(args) {
    if (args.isValidate) {
        archivoIncidenciasDialog.hide();
    }
}
function  handleDiasAdelantados(args) {
    if (args.isValidate) {
        searchDialogAdelantados.show();
    }
}
function handleCreate(args) {
    if (args.isValidate) {
        createDialog.hide();
    }
}
function handleAcumulados(args) {
    if (args.isValidate) {
        createDialog.hide();
    }
}
function handleUpdate(args) {
    if (args.isValidate) {
        updateDialog.hide();
    }
}
function handleCreateGrupo(args) {
    if (args.isValidate) {
        createDialogGrupo.hide();
    }
}
function handleCreateGrupoPago(args) {
    if (args.isValidate) {
        createDialogGrupoPago.hide();
    }
}
function handleCreatePension(args) {
    if (args.isValidate) {
        createDialogPension.hide();
    }
}
function handleUpdateGrupo(args) {
    if (args.isValidate) {
        updateDialogGrupo.hide();
    }
}
function handleUpdatePension(args) {
    if (args.isValidate) {
        updateDialogPension.hide();
    }
}
function handleDataTableSearch(args) {
    if (args.isValidate) {
        searchDialog.show();
    }
}
function handleDataTableRecibos(args) {
    if (args.isValidate) {
        recibosDialog.show();
    }
}
function handleLShow(args) {
    location.href = args.ruta;
}
function handleLoginRequest(args) {
    if (args.loggedIn) {
        location.href = args.ruta;
    }
}
function handlePasswordRequest(args) {
    if (args.isValidate) {
        recoverDialog.hide();
    }
}
function handleLogoutRequest(args) {
    if (args.loggetOut) {
        location.href = args.ruta;
    }
}
function handleLCreateEmpleadoRequest(args) {
    if (args.isValidate) {
        createDialog.hide();
        updateDialog.show();
    }
}
function handleCreateGrupo(args) {
    if (args.isValidate) {
        createDialogGrupo.hide();
    }
}
function handleUpdateGrupo(args) {
    if (args.isValidate) {
        updateDialogGrupo.hide();
    }
}
function handleCreateLaboral(args) {
    if (args.isValidate) {
        createDialogLaboral.hide();
    }
}
function handleUpdateLaboral(args) {
    if (args.isValidate) {
        updateDialogLaboral.hide();
    }
}
function handleCreateDocumento(args) {
    if (args.isValidate) {
        createDialogDocumento.hide();
    }
}
function handleUpdateDocumento(args) {
    if (args.isValidate) {
        updateDialogDocumento.hide();
    }
}
function handleCreateDocumentoCia(args) {
    if (args.isValidate) {
        createDialogDocumentoCia.hide();
    }
}
function handleUpdateDocumentoCia(args) {
    if (args.isValidate) {
        updateDialogDocumentoCia.hide();
    }
}
function handleCreateContacto(args) {
    if (args.isValidate) {
        createDialogContacto.hide();
    }
}
function handleUpdateContacto(args) {
    if (args.isValidate) {
        updateDialogContacto.hide();
    }
}
function handleCreateContactoCia(args) {
    if (args.isValidate) {
        createDialogContactoCia.hide();
    }
}
function handleUpdateContactoCia(args) {
    if (args.isValidate) {
        updateDialogContactoCia.hide();
    }
}
function handleCreatePais(args) {
    if (args.isValidate) {
        createDialogPais.hide();
    }
}
function handleUpdatePais(args) {
    if (args.isValidate) {
        updateDialogPais.hide();
    }
}
function handleCreateEstado(args) {
    if (args.isValidate) {
        createDialogEstado.hide();
    }
}
function handleUpdateEstado(args) {
    if (args.isValidate) {
        updateDialogEstado.hide();
    }
}
function handleCreateMunicipio(args) {
    if (args.isValidate) {
        createDialogMunicipio.hide();
    }
}
function handleUpdateMunicipio(args) {
    if (args.isValidate) {
        updateDialogMunicipio.hide();
    }
}
function handleCreateCiudad(args) {
    if (args.isValidate) {
        createDialogCiudad.hide();
    }
}
function handleUpdateCiudad(args) {
    if (args.isValidate) {
        updateDialogCiudad.hide();
    }
}
function handleSchedule(args) {
    if (args.isValidate) {
        searchSchedule.show();
    }
}
function handleCreateCompania(args) {
    if (args.isValidate) {
        createDialogCompania.hide();
    }
}
function handlelShowRecibos(args) {
    if (args.isValidate) {
        location.href = args.ruta;
    } else {
        jhandleLoginRequestQuery('#formLogin').effect("shake", {times: 3}, 100);
    }
}
function  handleCreateInformacionVacaciones(args) {
    if (args.isValidate) {
        createDialog.hide();
        informacionDialog.show();
    }
}
function  handleCreateSolicitudVacaciones(args) {
    if (args.isValidate) {
        loadDialogVacaciones.hide();
        informacionBitacoraDialog.show();

    }
}
function  handleCreateAdelantoVacaciones(args) {
    if (args.isValidate) {
        informacionDialog.hide();
    }
}
function  handleCreateInicidencia(args) {
    if (args.isValidate) {
        createDialog.show();
    }
}
function  handleCreateInicidenciaArchivo(args) {
    if (args.isValidate) {
        incidenciasDialog.show();
    }
}
function  handleCloseInicidenciaArchivo(args) {
    if (args.isValidate) {
        archivoIncidenciasDialog.hide();
    }
}
function  handleDiasAdelantados(args) {
    if (args.isValidate) {
        searchDialogAdelantados.show();
    }
}
function handleCreate(args) {
    if (args.isValidate) {
        createDialog.hide();
    }
}
function handleUpdate(args) {
    if (args.isValidate) {
        updateDialog.hide();
    }
}
function handleCreateGrupo(args) {
    if (args.isValidate) {
        createDialogGrupo.hide();
    }
}
function handleCreateGrupoPago(args) {
    if (args.isValidate) {
        createDialogGrupo.hide();
    }
}
function handleCreateRelacionLaboral(args) {
    if (args.isValidate) {
        createDialog.hide();
    }
}
function handleCreatePension(args) {
    if (args.isValidate) {
        createDialogPension.hide();
    }
}
function handleUpdateGrupo(args) {
    if (args.isValidate) {
        updateDialogGrupo.hide();
    }
}
function handleUpdatePension(args) {
    if (args.isValidate) {
        updateDialogPension.hide();
    }
}
function handleCreateConcepto(args) {
    if (args.isValidate) {
        createDialogConcepto.hide();
    }
}
function handleUpdateConcepto(args) {
    if (args.isValidate) {
        updateDialogConcepto.hide();
    }
}

function handleDataTableSearch(args) {
    if (args.isValidate) {
        searchDialog.show();
    }
}
function handleDataTableRecibos(args) {
    if (args.isValidate) {
        recibosDialog.show();
    }
}
function handleLShow(args) {
    location.href = args.ruta;
}
function handleLoginRequest(args) {
    location.href = args.ruta;
}
function handlePasswordRequest(args) {
    if (args.isValidate) {
        recoverDialog.hide();
    }
}
function handleLogoutRequest(args) {
    if (args.loggetOut) {
        location.href = args.ruta;
    }
}
function handleLCreateEmpleadoRequest(args) {
    if (args.isValidate) {
        createDialog.hide();
        updateDialog.show();
    }
}
function handleCreateGrupo(args) {
    if (args.isValidate) {
        createDialogGrupo.hide();
    }
}
function handleUpdateGrupo(args) {
    if (args.isValidate) {
        updateDialogGrupo.hide();
    }
}
function handleCreateLaboral(args) {
    if (args.isValidate) {
        createDialogLaboral.hide();
    }
}
function handleUpdateLaboral(args) {
    if (args.isValidate) {
        updateDialogLaboral.hide();
    }
}
function handleCreateDocumento(args) {
    if (args.isValidate) {
        createDialogDocumento.hide();
    }
}
function handleUpdateDocumento(args) {
    if (args.isValidate) {
        updateDialogDocumento.hide();
    }
}
function handleCreateContacto(args) {
    if (args.isValidate) {
        createDialogContacto.hide();
    }
}
function handleUpdateContacto(args) {
    if (args.isValidate) {
        updateDialogContacto.hide();
    }
}
function handleCreatePais(args) {
    if (args.isValidate) {
        createDialogPais.hide();
    }
}
function handleUpdatePais(args) {
    if (args.isValidate) {
        updateDialogPais.hide();
    }
}
function handleCreateEstado(args) {
    if (args.isValidate) {
        createDialogEstado.hide();
    }
}
function handleUpdateEstado(args) {
    if (args.isValidate) {
        updateDialogEstado.hide();
    }
}
function handleCreateMunicipio(args) {
    if (args.isValidate) {
        createDialogMunicipio.hide();
    }
}
function handleUpdateMunicipio(args) {
    if (args.isValidate) {
        updateDialogMunicipio.hide();
    }
}
function handleCreateCiudad(args) {
    if (args.isValidate) {
        createDialogCiudad.hide();
    }
}
function handleUpdateCiudad(args) {
    if (args.isValidate) {
        updateDialogCiudad.hide();
    }
}
function handleSchedule(args) {
    if (args.isValidate) {
        searchSchedule.show();
    }
}
function handleCreateCompania(args) {
    if (args.isValidate) {
        createDialogCompania.hide();
    }
}
function handleCreateRegistroPatronal(args) {
    if (args.isValidate) {
        createDialogRegistroPatronal.hide();
    }
}
function handleUpdateCompania(args) {
    if (args.isValidate) {
        updateDialogCompania.hide();
    }
}
function handleUpdateRegistroPatronal(args) {
    if (args.isValidate) {
        updateDialogRegistroPatronal.hide();
    }
}
function handleCreateTurno(args) {
    if (args.isValidate) {
        createDialogTurno.hide();
    }
}
function handleUpdateTurno(args) {
    if (args.isValidate) {
        updateDialogTurno.hide();
    }
}
function handleCreateTipoSueldo(args) {
    if (args.isValidate) {
        createDialogTipoSueldo.hide();
    }
}
function handleUpdateTipoSueldo(args) {
    if (args.isValidate) {
        updateDialogTipoSueldo.hide();
    }
}
function handleCreateDepartamento(args) {
    if (args.isValidate) {
        createDialogDepartamento.hide();
    }
}
function handleUpdateDepartamento(args) {
    if (args.isValidate) {
        updateDialogDepartamento.hide();
    }
}
function handleCreatePuesto(args) {
    if (args.isValidate) {
        createDialogPuesto.hide();
    }
}
function handleUpdatePuesto(args) {
    if (args.isValidate) {
        updateDialogPuesto.hide();
    }
}
function handleStatusDialog(args) {
    if (args.isValidate) {
        statusDialog.hide();
        actualizarDialog.hide();
    }
}
function handleActualizarDialog(args) {
    if (args.isValidate) {
        actualizarDialog.hide();
    }
}
function  handleCreateIncFiniquitos(args) {
    if (args.isValidate) {
        createDialog.show();
    }
}
function handleAcumulados(args) {
    if (args.isValidate) {
        acumuladosDialog.hide();
    }
}