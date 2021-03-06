import React, { Component } from 'react';

//MATERIAL UI STUFF
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import PropTypes from 'prop-types';
import withStyles from '@material-ui/core/styles/withStyles';

//COMPONENTES AUXILIARES
import ButtonEnReparto from '../Buttons/ButtonEnReparto';
import ButtonEntregado from '../Buttons/ButtonEntregado';
import ButtonPDF from '../Buttons/ButtonPDF'; 

//Redux stuff
import { connect } from 'react-redux';


const useStyles = makeStyles({
  root: {
    width: '100%',
  },
  container: {
    maxHeight: 440,
  }
});

const StyledTableRow = withStyles((theme) => ({
  root: {
    '&:nth-of-type(odd)': {
      backgroundColor: theme.palette.action.hover,
    },
  },
}))(TableRow);

export class TablaPedidosHoy extends Component{

    constructor(props){
        super(props);
        this.state = {
            page:0,
            rowsPerPage:10,
            checked:false,
            columns: [
              {id: 'e6' ,label:'Acciones',  mindWidth:300,align:'center'},
                { id: 'e1', label: 'ID', minWidth: 170, align:'left'},
                { id: 'e4', label: 'Cliente', minWidth: 100,align:'left' },
                {
                  id: 'e2',
                  label: 'Direccion',
                  minWidth: 170,
                  align: 'left',
                  format: (value) => value.toLocaleString('es-ES'),
                },
                {
                  id: 'e3',
                  label: 'Estado',
                  minWidth: 170,
                  align: 'left',
                  format: (value) => value.toLocaleString('es-ES'),
                },
                {
                  id: 'e5',
                  label: 'Vehículo',
                  minWidth: 170,
                  align: 'left',
                },    {
                  id: 'e7',
                  label: 'Factura',
                  minWidth: 170,
                  align: 'left',
                }
            ]
            }
        }


        handleChangePage = (event, newPage) => {
            this.setState({
                page:newPage
            })
        };

        handleChangeRowsPerPage = (event) => {
            this.setState({
                rowsPerPage:+event.target.value,
                page:0
                })

        };

render(){
    const  classes  = this.props;

  return (

    <Paper className={classes.root}>
      <TableContainer className={classes.container}>
        <Table stickyHeader aria-label="sticky table">
          <TableHead > 
            <TableRow>
              {this.state.columns.map((column) => (      
                                
                <TableCell
                  key={column.id}
                  align={column.align}
                  style={{ minWidth: column.minWidth,
                    fontWeight:"bold",
                    backgroundColor:"#d4e6f1"
                  }}
                >
                  {column.label}
                </TableCell>
              ))}
              
            </TableRow>
          </TableHead>
          <TableBody>

            {this.props.datos.slice(this.state.page * this.state.rowsPerPage, this.state.page * this.state.rowsPerPage + this.state.rowsPerPage).map((row) => {
              return (
                <React.Fragment>
        
                <StyledTableRow role="checkbox" tabIndex={-1} key={row.code}>       
                   
                  {this.state.columns.map((column) => {
                    
                    const value = column.id==='e7'?<ButtonPDF color = "secondary" iconColor = "white" idPedido={row.e1}/>:
                                    column.id === 'e6' & row.e3 === 'PREPARADO'?
                                    <ButtonEnReparto activacion={row.e3} idPedido = {row.e1} />:
                                    column.id === 'e6' & row.e3 === 'EN_REPARTO'?
                                    <ButtonEntregado  activacion={row.e3} idPedido = {row.e1} />:
                                    row[column.id]
                    return (
                      <TableCell key={column.id} align={column.align}>
                        {column.format && typeof value === 'number' ? column.format(value) : value}
                      </TableCell>
                    );
                  })}

                </StyledTableRow>
                </React.Fragment> );
              
            }
            )}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        rowsPerPageOptions={[2, 4, 6]}
        component="div"
        count={this.props.datos.length}
        rowsPerPage={this.state.rowsPerPage}
        page={this.state.page}
        labelRowsPerPage="Elementos por pagina:"
        onChangePage={this.handleChangePage}
        onChangeRowsPerPage={this.handleChangeRowsPerPage}
      />
    </Paper>
  );
}
}

TablaPedidosHoy.propTypes = {
    classes: PropTypes.object.isRequired,
    data: PropTypes.object.isRequired,

}

const mapStateToProps = (state) => ({
    data:state.data
})

const mapActionsToProps = {
}

export default connect(mapStateToProps, mapActionsToProps)(TablaPedidosHoy)