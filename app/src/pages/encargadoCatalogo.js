import React, { Component } from 'react';
import PropTypes from 'prop-types';
import withStyles from '@material-ui/core/styles/withStyles';

//Redux Stuff
import { connect } from 'react-redux';
import { loadProductos } from '../redux/actions/dataActions';

//Components
import Topbar from '../components/Topbar';
import TablaCatalogoEncargado from '../components/TablaCatalogoEncargado';

const style = {

}

class encargadoCatalogo extends Component {
    constructor(props){
        super(props);
        this.state ={
            data: []
        }
    }

    componentDidMount(){
        this.props.loadProductos();
    }

    render() {
        const {classes, data} = this.props;

        return (
            <div>
                <div className={classes.main}>
                  { 
                    data.length ===0? null: data.categorias.map((row) => 
                        <TablaCatalogoEncargado categoria={row} productos = {data.productos}/>
                    )
                  }
                </div>
            </div>
        )
    }
}

encargadoCatalogo.propTypes = {
    classes: PropTypes.object.isRequired,
    data: PropTypes.object.isRequired,
    loadProductos: PropTypes.func.isRequired
}

const mapStateToProps = (state) => ({
    data: state.data
})

const mapActionsToProps = {
    loadProductos
}

export default connect(mapStateToProps, mapActionsToProps)(withStyles(style)(encargadoCatalogo))
