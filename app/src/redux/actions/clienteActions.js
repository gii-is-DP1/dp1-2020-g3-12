import { SET_CLIENTES,CLEAR_CLIENTES,SET_FACTURAS,CLEAR_FACTURAS , SET_ERRORS,CLEAR_ERRORS } from '../types';
import axios from 'axios';


export const getClientes = () => (dispatch) => {

    axios.get("/cliente")
        .then((res) => {
            dispatch({
                type:SET_CLIENTES,
                payload:res.data
            })
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data.message
                })
            } else {
               console.log(err)
            }
        })
}

export const getFacturasCliente = (id) => (dispatch) => {
    axios.get(`/facturaEmitida/${id}`)
        .then((res) => {
            dispatch({
                type:SET_FACTURAS,
                payload:res.data
            })
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data.message
                })
            } else {
               console.log(err)
            }
        })
}

export const getFacturasClienteAndFecha = (datos) => (dispatch) => {
    axios.post('/facturaEmitida/clienteFecha',datos)
        .then((res) => {
            dispatch({
                type:SET_FACTURAS,
                payload:res.data
            })
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data.message
                })
            } else {
               console.log(err)
            }
        })
}


export const sendDevolucion = (datos) => (dispatch) => {
    axios.post('/facturaEmitida/devolucion',datos)
        .then(() => {
            dispatch({type: CLEAR_ERRORS})      
        })
        .catch((err) => {
            if(err.response){
                dispatch({
                    type: SET_ERRORS,
                    payload: err.response.data.message
                })
            } else {
               console.log(err)
            }
        })
}

