import React,{Component} from 'react';
import withStyles from '@material-ui/core/styles/withStyles';
import GresurImage from '../images/Gresur_login.png';
import PropTypes from 'prop-types';

//MUI Stuff
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Avatar from '@material-ui/core/Avatar';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';
import gresur from '../images/Gresur_transparente.png'
import Snackbar from '../components/Other/SnackBar'

//Redux stuff
import { connect } from 'react-redux';
import { loginUser,clear } from '../redux/actions/userActions';


const style = {
    root: {
        flexGrow: 1, 
      },
    grid:{
        marginTop:'5%',
    },
    paper: {
        padding: 0,
        textAlign: 'center',
        opacity:0.8
    },
    textField:{
        margin:'15px auto 15px auto'
    },
    button:{
        marginTop: 25
    },
    large: {
        marginLeft:14,
        width: 100,
        height: 100,
      },
    progress:{
        position: 'absolute'
    },
    InicioSesion:{
        color: '#ffa200',
        marginTop: 20,
        fontFamily: 'Courier',
        fontWeight: 800,
        fontSize: 40,
        width: "100%"
    }
  }

class login extends Component {

    constructor(){
        super();
        this.state = {
            username:'',
            password:'',
            enviar:false,
            data:null,
            errores:{password:[], username:[]}
        }
    }

    componentDidMount() {
        this.props.clear()
    }

    componentDidUpdate(prevProps,prevState){

        if(prevState.enviar != this.state.enviar && this.state.enviar){
            var valid = true

            if(this.state.username.length<1){
                this.setState(state=>({
                    errores:{
                        ...state.errores,
                        username:['Usuario vacio']
                    }
                }))
                valid = false;
            }else{
                this.setState(state=>({
                    errores:{
                        ...state.errores,
                        username:[]
                    }
                }))
            }

            if(this.state.password.length<6){
                this.setState(state=>({
                    errores:{
                        ...state.errores,
                        password:['Debe ser mayor de 6 caracteres']
                    }
                }))
                valid = false;
            }else{
                this.setState(state=>({
                    errores:{
                        ...state.errores,
                        password:[]
                    }
                }))
            }
            if(valid){
                this.props.loginUser(this.state.data,this.props.history);
            }
            
            this.setState({
                enviar:false
            })
        }
    }

    handleSubmit = (event) =>  {
        event.preventDefault();
         const userData = {
             username: this.state.username,
             password: this.state.password
         };
         this.setState({
             enviar:true,
             data:userData
         })
     }

    

    handleChange = (event) =>  {
        this.setState(state=>({
            [event.target.name]:event.target.value,
            errores:{
                ...state.errores,
                [event.target.name]:[]
            }
        }))
        this.props.clear()
    }

    render() {

        document.body.style.background = `url(${GresurImage}) no-repeat center center fixed`;
        document.body.style.backgroundSize = "cover";

        const {classes, UI:{loading,errors}} = this.props;
        return (
            
            <div>

                <div className={classes.root}>

                <Snackbar type = "error" open = {errors} truco ={true} message = {errors}></Snackbar>
                {errors ? document.getElementById("botonSnack").click():null}


                <Grid container spacing={0} className={classes.grid}>
                    <Grid item xs/>
                    <Grid item xs={6}>
                    <Paper className={classes.paper} elevation={2}>
                        <div className={classes.paperItems}>
                        <Grid container spacing={3} className={classes.grid}>
                            <Grid item xs>
                            <Avatar src={gresur} variant='square' className={classes.large}/>
                            </Grid>
                            <Grid item xs={6}>
                                <Typography variant='h4' className = {classes.InicioSesion}>
                                   <u>Inicio de sesión</u> 
                                </Typography>
                                <form  noValidate onSubmit={this.handleSubmit}>
                                    <TextField fullWidth id="username" name="username" label="Username" onChange={this.handleChange} className={classes.textField} 
                                        error={errors||this.state.errores.username.length>0}  helperText={this.state.errores.username[0]} value={this.state.username}/>
                                    <TextField fullWidth id="password" name="password" label="Password" type="password" onChange={this.handleChange} className={classes.textField} 
                                        error={errors||this.state.errores.password.length>0} helperText={this.state.errores.password[0]} value={this.state.password}/>

                                    <Button type="submit" variant="contained" color="primary" className={classes.button} disabled={loading}>
                                    Login
                                        {loading && (
                                            <CircularProgress size={20} className={classes.progress}/>
                                        )}
                                    </Button>
                                </form>
                            </Grid>
                            <Grid item xs/>
                        </Grid> 
                        </div>
                    </Paper> 
                    </Grid>
                    <Grid item xs/>
                </Grid>  
                </div>
            </div>
        )
    }
}

login.propTypes={
    classes: PropTypes.object.isRequired,
    loginUser: PropTypes.func.isRequired,
    user:PropTypes.object.isRequired,
    UI:PropTypes.object.isRequired
}

const mapStateToProps = (state) => ({
    user: state.user,
    UI: state.UI
});

const mapActionsToProps = {
    loginUser,
    clear
}

export default connect(mapStateToProps,mapActionsToProps)(withStyles(style)(login))
