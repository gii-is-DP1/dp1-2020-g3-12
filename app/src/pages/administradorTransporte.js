import React, { Component } from 'react';
import PropTypes from 'prop-types';
import withStyles from '@material-ui/core/styles/withStyles';
import { connect } from 'react-redux';

//Redux stuff
import { loadVehiculosSeguroITVReparacionPaged , loadPedidosPaginados, clear} from '../redux/actions/dataActions';

//Components

import VerticalTabs from '../components/Other/VerticalTabsAdmin';
import SnackCallController from '../components/Other/SnackCallController';


const style = {  
    verticalTabs: {
    }
}

class administradorTransporte extends Component {
    constructor(props){
        super(props);
        this.state ={
            data: []
        }
    }

    componentDidMount(){
        this.props.loadVehiculosSeguroITVReparacionPaged(0,2);
        this.props.loadPedidosPaginados("",0,2);
    }

    componentWillUnmount(){
        this.props.clear();
    }

    render() {
        const {classes, data,UI:{errors,enviado}} = this.props;

        return (

            <div>  
                <SnackCallController  enviado = {enviado} message = {"Operacion realizada correctamente"} errors={errors} />
                <VerticalTabs className={classes.verticalTabs} datos = {data}/>
            </div>
        )
    }
}

administradorTransporte.propTypes = {
    classes: PropTypes.object.isRequired,
    data: PropTypes.object.isRequired,
    loadVehiculosSeguroITVReparacion: PropTypes.func.isRequired,
    loadPedidos: PropTypes.func.isRequired
}

const mapStateToProps = (state) => ({
    data: state.data,
    UI: state.UI
})

const mapActionsToProps = {
    loadVehiculosSeguroITVReparacionPaged,
    loadPedidosPaginados,
    clear
}

export default connect(mapStateToProps, mapActionsToProps)(withStyles(style)(administradorTransporte))