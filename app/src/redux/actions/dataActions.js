import { SET_ALMACENGESTIONENCARGADO, CLEAR_ALMACENGESTIONENCARGADO, SET_BALANCE,SET_ENVIADO, CLEAR_BALANCE, SET_PEDIDO, CLEAR_PEDIDO, CLEAR_ALMACENGESTION, SET_ALMACENGESTION, CLEAR_CLIENTE, SET_CLIENTE, CLEAR ,CLEAR_ISDEFAULTER, SET_PEDIDOS, SET_ERRORS, CLEAR_PEDIDOS, LOADING_UI, CLEAR_ERRORS, SET_PRODUCTOS, CLEAR_PRODUCTOS,SET_PERSONAL,CLEAR_PERSONAL, SET_VEHICULOS, CLEAR_VEHICULOS, SET_OCUPACION, CLEAR_OCUPACION, SET_ISDEFAULTER, SET_VEHICULOSITVSEGUROREPARACION, CLEAR_VEHICULOSITVSEGUROREPARACION,SET_CONTRATO,SET_ALMACEN,CLEAR_CONTRATO,SET_FACTURA, SET_TIPOSVEHICULOS, CLEAR_TIPOSVEHICULOS,SET_USER  } from '../types';
import axios from 'axios';
import {getUserData} from './userActions';
import {getProductosPaginados} from './productoActions'
export const loadPedidos = (orden="DEFAULT") => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/pedido/${orden}`)
        .then((res) => {
            dispatch({type: SET_PEDIDOS, payload: res.data})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadPedidosPaginados = (orden="",pageNo,pageSize) => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/pedido/page=${pageNo}&size=${pageSize}&order=${orden}`)
        .then((res) => {
            dispatch({type: SET_PEDIDOS, payload: {content:res.data.content,totalElements:res.data.totalElements,totalPages:res.data.totalPages}})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadPedidoById = (id) => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/pedido/id/${id}`)
        .then((res) => {
            dispatch({type: SET_PEDIDO, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadPedidosByEstado = (estado, orden="DEFAULT") => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/pedido/${estado}/${orden}`)
        .then((res) => {
            dispatch({type: SET_PEDIDOS, payload: res.data});
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadPedidosByEstadoPaginado = (estado, orden="",pageNo,pageSize) => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/pedido/${estado}/page=${pageNo}&size=${pageSize}&order=${orden}`)
        .then((res) => {
            dispatch({type: SET_PEDIDOS, payload: {content:res.data.content,totalElements:res.data.totalElements,totalPages:res.data.totalPages}});
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearPedidos = () => (dispatch) => {
    dispatch({type: CLEAR_PEDIDOS})
}

export const cancelarPedido = (id, estado="TODO", orden="",pageNo,pageSize) => (dispatch) => {

    axios.post(`/pedido/${id}`)
        .then((res) => {
            estado === "TODO" ? dispatch(loadPedidosPaginados(orden,pageNo,pageSize)) : dispatch(loadPedidosByEstadoPaginado(estado, orden,pageNo,pageSize))
            dispatch({
                type: SET_ENVIADO,
            });
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })

}

export const loadProductos = () => (dispatch) => {
    dispatch({type: LOADING_UI})

    axios.get('/producto')
        .then((res) => {
            dispatch({type: SET_PRODUCTOS, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadProductosByNombre = (nombre) => (dispatch) => {
    dispatch({type: LOADING_UI})

    axios.get(`/producto/${nombre}`)
        .then((res) => {
            dispatch({type: SET_PRODUCTOS, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearProductos = () => (dispatch) => {
    dispatch({type: CLEAR_PRODUCTOS})
}

export const loadPersonalContrato = (rol="TODOS") => function (dispatch) {
    dispatch({type: LOADING_UI})
    
    axios.get(`/contrato/${rol}`)
        .then((res) => {
            console.log(rol)
            dispatch({type: SET_CONTRATO, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadPersonal = () => function (dispatch) {
    dispatch({type: LOADING_UI})
    
    axios.get('/adm/personal')
        .then((res) => {
            dispatch({type: SET_PERSONAL, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const deleteContrato = (nif) => function (dispatch){
    dispatch({type: LOADING_UI})
    axios.delete(`/contrato/delete/${nif}`)
        .then((res) => {
            axios.put(`/adm/almacen/${nif}`)
            .catch(err=>{
                console.log(err)
            })
            dispatch(loadPersonalContrato())
            dispatch({
                type: SET_ENVIADO,
            });
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
               console.log(err)
            }
        })
}
export const loadAlmacen = () => function (dispatch){
    dispatch({type: LOADING_UI})
    
    axios.get('/almacen')
        .then((res) => {
            dispatch({type: SET_ALMACEN, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })


}

export const loadAlmacenDisponible = () => function (dispatch){
    dispatch({type: LOADING_UI})
    
    axios.get('/encargado/almacen')
        .then((res) => {
            dispatch({type: SET_ALMACEN, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })


}

export const clearPersonal = () => (dispatch) => {
    dispatch({type: CLEAR_PERSONAL})
    
}

export const loadPersonalProfile = () => function (dispatch) {
    dispatch({type: LOADING_UI})
    
    axios.get('/adm/personal/profile')
        .then((res) => {
            dispatch({type: SET_USER, payload: res})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}
 
export const putPersonalProfile = (personal) => function (dispatch) {
    axios.put('/adm/personal/profile',personal)    
    .then((res) => {
        //dispatch(loadPersonalContrato());
        dispatch(getUserData());
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    })
}

export const putPersonalProfilePassword = (personal) => function (dispatch) {
    axios.put('/auth/password',personal)    .then((res) => {
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    })
}


export const addPersonal = (personal,rolEmpleado,contrato) => (dispatch) =>{
        axios.post(`/adm/add/${rolEmpleado}`,personal)
        .then((res) => {
            dispatch(addContrato(personal.nif,contrato));
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        }) 
}

export const addContrato = (nif,contrato) => (dispatch) =>{
    axios.post(`/contrato/add/${nif}`,contrato)
    .then((res) => {
        dispatch(loadPersonalContrato());
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    }) 
}

export const editContrato = (nif,contrato) => (dispatch) =>{
    axios.put(`/contrato/update/${nif}`,contrato)
    .then((res) => {
        dispatch(loadPersonalContrato());
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    })
}

export const clearContrato = () => (dispatch) => {
    dispatch({type: CLEAR_CONTRATO})
}

export const enReparto = (id,vehiculo) => (dispatch) =>{
    axios.put(`/pedido/reparto/${id}`,vehiculo)
    .then((res) => {
        dispatch(loadPedidosHoy());
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    }) 
}

export const entregado = (id) => (dispatch) =>{
    axios.put(`/pedido/entregado/${id}`)
    .then((res) => {
        dispatch(loadPedidosHoy());
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    }) 
}

export const loadPedidosHoy = () => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get('/pedido/hoy')
        .then((res) => {
            dispatch({type: SET_PEDIDOS, payload: res.data})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}


export const transportistaEnReparto = () => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get('/auth/transportista')
        .then((res) => {
            dispatch({type: SET_PERSONAL, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadPedidosByEstadoTransportista = (estado) => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/pedido/transportista/${estado}`)
        .then((res) => {
            dispatch({type: SET_PEDIDOS, payload: res.data});
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadFactura = (id) => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/pedido/factura/${id}`)
        .then((res) => {
            dispatch({type: SET_FACTURA, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}


export const loadVehiculosITVSeguroDisponibilidadByTransportista = () => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.get('/vehiculo')
        .then((res) => {
            dispatch({type: SET_VEHICULOS, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearVehiculos = () => (dispatch) => {
    dispatch({type: CLEAR_VEHICULOS})
}

export const setProducto = (producto,page,categoria) => (dispatch) => {

    axios.post('/producto/save', producto)
        .then((res) => {
            dispatch(getProductosPaginados(page,categoria,null,5,''))
            dispatch({
                type: SET_ENVIADO,
            });
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadOcupacionVehiculosEnReparto = () => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.get('/pedido/ocupacion')
        .then((res) => {
            dispatch({type: SET_OCUPACION, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearOcupacion = () => (dispatch) => {
    dispatch({type: CLEAR_OCUPACION})
}


export const loadClienteIsDefaulter = (NIF) => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.get(`/cliente/${NIF}/isDefaulter`)
        .then((res) => {
            dispatch({type: SET_ISDEFAULTER, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearClienteIsDefaulter = () => (dispatch) => {
    dispatch({type: CLEAR_ISDEFAULTER})
}


export const loadCliente = (NIF) => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.get(`/cliente/${NIF}`)
        .then((res) => {
            dispatch({type: SET_CLIENTE, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearClienteByNIF = () => (dispatch) => {
    dispatch({type: CLEAR_CLIENTE})
}

export const clear = () => (dispatch) => {
    dispatch({type: CLEAR})
}

export const loadAlmacenGestion = () => (dispatch) => {
    dispatch({type: LOADING_UI})
    axios.get(`/almacen/gestion`)
        .then((res) => {
            dispatch({type: SET_ALMACENGESTION, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}
export const clearAlmacenGestion = () => (dispatch) => {
    dispatch({type: CLEAR_ALMACENGESTION})
}

export const loadAlmacenGestionEncargado = (almacenAdm) => (dispatch) => {
    dispatch({type: LOADING_UI})
    axios.get(`/almacen/gestionEncargado/${almacenAdm}`)
        .then((res) => {
            dispatch({type: SET_ALMACENGESTIONENCARGADO, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const updateEstanteriaCapacidad = (categoria, capacidad, almAdm, version) => (dispatch) =>{
    axios.put(`/estanterias/update/${categoria}/${capacidad}/${version}`)
    .then((res) => {
        dispatch(loadAlmacenGestionEncargado(almAdm));
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
        //dispatch(loadAlmacenGestionEncargado(almAdm));
    })
}

export const clearAlmacenGestionEncargado = () => (dispatch) => {
    dispatch({type: CLEAR_ALMACENGESTIONENCARGADO})
}

export const loadVehiculosSeguroITVReparacion = () => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.get('/vehiculo/all')
        .then((res) => {
            dispatch({type: SET_VEHICULOSITVSEGUROREPARACION, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadVehiculosSeguroITVReparacionPaged = (pageNumber,size) => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.get(`/vehiculo/allpaginado?page=${pageNumber}&size=${size}`)
        .then((res) => {
            dispatch({type: SET_VEHICULOSITVSEGUROREPARACION, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearVehiculosSeguroITVReparacion = () => (dispatch) => {
    dispatch({type: CLEAR_VEHICULOSITVSEGUROREPARACION})
    
}

export const addVehiculo = (vehiculo,pageNo,size) => (dispatch) =>{
    axios.post(`/vehiculo/add/`, vehiculo)
    .then((res) => {
        dispatch(loadVehiculosSeguroITVReparacionPaged(pageNo,size));
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    }) 
}

export const loadTiposVehiculos = () => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.get('/vehiculo/allTiposVehiculos')
        .then((res) => {
            dispatch({type: SET_TIPOSVEHICULOS, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearTiposVehiculos = () => (dispatch) => {
    dispatch({type: CLEAR_TIPOSVEHICULOS})
    
}

export const deleteVehiculo = (matricula) => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.delete(`/vehiculo/delete/${matricula}`)
        .then((res) => {
            dispatch(loadVehiculosSeguroITVReparacion())
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const loadBalance = (year) => (dispatch) => {
    
    dispatch({type: LOADING_UI})

    axios.get(`/balance/${year}`)
        .then((res) => {
            dispatch({type: SET_BALANCE, payload: res})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const setEstaPagadoFacturaE = (id,estado="TODO",orden="", pageNo,pageSize) => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.put(`/pedido/pagado/${id}`)
        .then((res) => {
            estado === "TODO" ? dispatch(loadPedidosPaginados(orden,pageNo,pageSize)) : dispatch(loadPedidosByEstadoPaginado(estado, orden,pageNo,pageSize))
            dispatch({
                type: SET_ENVIADO,
            });
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const updatePedido = (estado="TODO",orden="", pedido, pageNo,pageSize) => function (dispatch) {
    dispatch({type: LOADING_UI})

    axios.put('/pedido/update/', pedido)
        .then((res) => {
            estado === "TODO" ? dispatch(loadPedidosPaginados(orden,pageNo,pageSize)) : dispatch(loadPedidosByEstadoPaginado(estado, orden,pageNo,pageSize))
            dispatch({
                type: SET_ENVIADO,
            });
        })

        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
            estado === "TODO" ? dispatch(loadPedidosPaginados(orden,pageNo,pageSize)) : dispatch(loadPedidosByEstadoPaginado(estado, orden,pageNo,pageSize))
        })
}


export const loadFacturaEmitida = (numFactura) => (dispatch) => {

    dispatch({type: LOADING_UI})

    axios.get(`/facturaEmitida/cargar/${numFactura}`)
        .then((res) => {
            dispatch({type: SET_FACTURA, payload: res})
            dispatch({type: CLEAR_ERRORS})
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data
                })
            } else {
                console.log(err)
            }
        })
}

export const clearFacturaEmitida = () => (dispatch) => {
    dispatch({type: CLEAR})
}

export const rectificaFactura = (factura) => (dispatch) => {
    
    axios.post(`/facturaEmitida/rectificar`, factura)
    .then(()=>{
        dispatch({
            type: SET_ENVIADO,
        });
    })
    .catch((err) => {
        if(err.response){
            dispatch({
                type: SET_ERRORS,
                payload: err.response.data
            })
        } else {
            console.log(err)
        }
    })

}

export const clearLoading = () => (dispatch) => {
    dispatch({type: CLEAR_ERRORS})
}