import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import CalendarTodayIcon from '@material-ui/icons/CalendarToday';
import TimerIcon from '@material-ui/icons/Timer';
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';
import EventIcon from '@material-ui/icons/Event';
import LocalShippingIcon from '@material-ui/icons/LocalShipping';
import PermContactCalendarIcon from '@material-ui/icons/PermContactCalendar';
import RecentActorsIcon from '@material-ui/icons/RecentActors';
import EmailIcon from '@material-ui/icons/Email';
import PhoneIcon from '@material-ui/icons/Phone';
import DoneAllIcon from '@material-ui/icons/DoneAll';
import { FaBox } from "react-icons/fa";
import Button from '@material-ui/core/Button';
import { useDispatch } from "react-redux";
import { useSelector } from "react-redux";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import CancelIcon from '@material-ui/icons/Cancel';
import Tooltip from '@material-ui/core/Tooltip'



import { cancelarPedido, setEstaPagadoFacturaE } from '../../redux/actions/dataActions';
import PopUpModificarPedido from '../Dialogs/PopUpModificarPedido';
import ButtonPDF from '../Buttons/ButtonPDF';


const useStyles = makeStyles((theme) => ({
  main: {
    display: 'inline-block',
    padding: '10px 20px 10px 20px',
    width: '40vw',
    userSelect: 'none',
    position: 'relative',
    minWidth: 692,
  },

  productosPedido: {
    display: 'grid',
    gridTemplateRows: '1fr',
    gridTemplateColumns: '0.1fr 1fr 0.5fr',
    width: '100%',
    alignContent: 'flex-start',
  },

  cantNombre: {
    gridColumn: '2',
    gridRow: '1'
  },

  precio: {
    gridColumn: '3',
    gridRow: '1',
    textAlign: 'center'
  }, 

  campoProductos: {
    overflowY: 'auto',
    height: 120,
  },

  borderMain: {
    borderColor: '#bdbdbd',
    borderStyle: 'solid',
    borderRadius: 24,
    border: 2,
    backgroundColor: '#f7f7f7',
    boxShadow: '0 4px 8px 0 rgba(0, 0, 0, 0.15), 0 6px 20px 0 rgba(0, 0, 0, 0.14)'
  }, 

  legend: {
    textDecoration: 'underline'
  }, 

  iconoObjeto: {
    display: 'flex',
    alignItems: 'center',
  },

  icono: {
    marginRight: 5,
  },

  iconoProductos: {
    gridRow: 1,
    gridColumn: 1,
    marginTop: 20
  },

  infoCliente: {
    borderColor: '#bdbdbd',
    borderRadius: 24,
    borderStyle: 'solid',
    border: 1,
  }, 

  datosContacto: {
    borderColor: '#bdbdbd',
    borderRadius: 24,
    borderStyle: 'solid',
    border: 1,
  },

  botonPagado: {
    marginLeft: 10,
    borderRadius: 20,
    fontWeight: 'bold'
  },

  botones:{
    display: 'flex',
  },

  botonCancelar: {
    marginLeft: 5,
    borderRadius: 20,
    fontWeight: 'bold'
  }

}));

export default function HistorialPedidoBox(props) {
  const classes = useStyles();

  const {pedido,estado,orden,pageNo,pageSize}=props

  const counter = useSelector(state => state);
  const dispatch = useDispatch();

  
  const handleSubmitPago = (id,event) => {
    event.preventDefault();
    dispatch(setEstaPagadoFacturaE(id, estado, orden ,pageNo,pageSize))
  }

  const handleSubmitCancelacion = (id,event) => {
    event.preventDefault();
    dispatch(cancelarPedido(id, estado, orden ,pageNo,pageSize))
  }

  return (
    <div className={classes.main}>
        <fieldset className={classes.borderMain}>
          <legend className={classes.legend}><b>PEDIDO-{pedido.id}</b></legend>          

          <div className={classes.iconoObjeto} style = {{justifyContent : 'space-between'}}>

          <span style = {{display: 'inline-flex', alignItems: 'center'}}>
            <CalendarTodayIcon className={classes.icono}/>
            <p><b>Fecha de realizacion:</b> {pedido.fechaRealizacion}</p>
          </span>

          { pedido.estado !== "EN_ESPERA" ? 
            <Tooltip arrow placement = 'top' 
              title = {<p style = {{fontSize: 12, margin: '0px 5px', width: 'max-content'}}>El pedido ya ha sido tramitado y no puede modificarse</p>}
            >
              <div className={classes.botones}>
                <PopUpModificarPedido pedido={pedido} estado={estado} orden={orden} pageNo={pageNo} pageSize={pageSize}/>
                <Button className={classes.botonCancelar} variant='outlined' color="secondary" size="small" disabled={true} onClick={(event) => handleSubmitCancelacion(pedido.id,event)}><b>CANCELAR</b></Button>
              </div>
            </Tooltip>
            :
            <div className={classes.botones}>
              <PopUpModificarPedido pedido={pedido} estado={estado} orden={orden} pageNo={pageNo} pageSize={pageSize}/>
              <Button className={classes.botonCancelar} variant='outlined' color="secondary" size="small" disabled={false} onClick={(event) => handleSubmitCancelacion(pedido.id,event)}><b>CANCELAR</b></Button>
            </div>
          }

          </div>

          <div className={classes.iconoObjeto}>
            <TimerIcon className={classes.icono}/>
            <p><b>Estado del pedido:</b>
            <span style={pedido.estado!=="CANCELADO"? 
              {fontSize: 16, marginLeft: 6, padding: 7, color: '#49C638'} : 
              {fontSize: 16, marginLeft: 6, padding: 7, color: '#FF3D3D'}}>
              <b>{pedido.estado}</b>
            </span>
            </p>
          </div>
          
          <div className={classes.iconoObjeto}>
            <MonetizationOnIcon className={classes.icono}/>
            <p><b>Pedido pagado:</b></p>


            <Button 
              disabled={pedido.estado==='CANCELADO'} 
              className={classes.botonPagado} 
              variant='outlined'
              color='secondary'
              style={pedido.facturaEmitida.estaPagada || pedido.estado==="CANCELADO"? {} : {color: '#FF3D3D', borderColor: '#FF3D3D'}} 
              size="small" 
              onClick={(event) => handleSubmitPago(pedido.id,event)}>

              {pedido.facturaEmitida.estaPagada? <CheckCircleIcon/> : <CancelIcon/>}
            </Button>          
          </div>

          <div className={classes.iconoObjeto}>
            <EventIcon className={classes.icono}/>
            <p><b>Fecha de envio:</b> {pedido.fechaEnvio}</p>
          </div>

          <div className={classes.iconoObjeto}>
            <LocalShippingIcon className={classes.icono}/>
            <p><b>Direccion de envio:</b> {pedido.direccionEnvio}</p>
          </div>
          
          <br/>

          <fieldset className={classes.infoCliente}>
              <legend className={classes.legend}><b>INFORMACION DEL CLIENTE</b></legend>

              <div className={classes.iconoObjeto}>
                <PermContactCalendarIcon className={classes.icono}/>
                <p><b>Nombre y apellidos:</b> {pedido.facturaEmitida.cliente.name} {pedido.facturaEmitida.cliente.apellidos}</p>
              </div>

              <div className={classes.iconoObjeto}>
                <RecentActorsIcon className={classes.icono}/>
                <p><b>NIF:</b> {pedido.facturaEmitida.cliente.nif}</p>
              </div>

              <fieldset className={classes.datosContacto}>
                  <legend className={classes.legend}><b>DATOS DE CONTACTO</b></legend>

                  <div className={classes.iconoObjeto}>
                    <EmailIcon className={classes.icono}/>
                    <p><b>Correo electronico:</b> {pedido.facturaEmitida.cliente.email}</p>
                  </div>

                  <div className={classes.iconoObjeto}>
                    <PhoneIcon className={classes.icono}/>
                    <p><b>Telefono:</b> {pedido.facturaEmitida.cliente.tlf}</p>
                  </div>
              </fieldset>

          </fieldset>

          <br/>

          <fieldset className={classes.campoProductos}>
              <legend className={classes.legend}><b>PRODUCTOS DEL PEDIDO</b></legend>
              {pedido.facturaEmitida.lineasFacturas.map((row) => (
                <div className={classes.productosPedido}>
                  <FaBox className={classes.iconoProductos}/>
                  <p className={classes.cantNombre}><b>{row.cantidad}</b> x {row.producto.nombre}</p>
                  <p className={classes.precio}>{row.precio}€</p>
                </div>
              ))}
          </fieldset>

          <br/>

          <div className={classes.iconoObjeto}>
            <DoneAllIcon className={classes.icono}/>
            <p><b>TOTAL DEL PEDIDO</b> ................................................................ {pedido.facturaEmitida.importe.toFixed(2)}€</p>
          </div>

        </fieldset>
        <ButtonPDF 
          style = {{position: 'absolute', right: 0, bottom: 0, borderRadius: '100%', height: '60px', width: '30px'}}
          idPedido = {pedido.id}
          color = "secondary"
          iconColor = "white"/>
    </div>
  );
}