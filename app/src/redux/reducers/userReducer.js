import { SET_USER, SET_AUTHENTICATED, SET_UNAUTHENTICATED, LOADING_USER, SET_NOTIFICACIONES_NO_LEIDAS , CLEAR_NOTIFICACIONES_NO_LEIDAS ,SET_NOTIFICACIONES_LEIDAS, SET_POSTPEDIDO } from '../types';

const initialState = {
    loading:false,
    authenticated: false,
    notificaciones:[],
    notificacionesLeidas:[],
    credentials: {},
    postPedido: null,
}

export default function(state = initialState,action){
    switch (action.type) {
        case SET_AUTHENTICATED:
            return {
                ...state,
                authenticated : true
            };
        case SET_UNAUTHENTICATED:
            return initialState;
        case SET_USER:
            return{
                authenticated: true,
                loading:false,
                ...action.payload
            };
        case LOADING_USER:
            return{
                ...state,
                loading: true
            };
        case SET_NOTIFICACIONES_NO_LEIDAS:
            return{
                ...state,
                notificaciones: action.payload
            }

        case SET_NOTIFICACIONES_LEIDAS:
            return{
                ...state,
                notificacionesLeidas: action.payload
            }
        
        case CLEAR_NOTIFICACIONES_NO_LEIDAS:
            return{
                ...state,
                notificaciones:[]
            }
        
        case SET_POSTPEDIDO:
            return {
                ...state,
                postPedido: action.payload
            }

        default:
            return state;
    }
}