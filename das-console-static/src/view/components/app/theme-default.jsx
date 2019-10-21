import getMuiTheme from 'material-ui/styles/getMuiTheme'
import {blue600, blueGrey900} from 'material-ui/styles/colors'

const themeDefault = getMuiTheme({
  palette: {
  },
  appBar: {
    height: 57,
    color: blue600
  },
  drawer: {
    width: 230,
    color: blueGrey900
  },
  raisedButton: {
    primaryColor: blue600,
  }
})


export default themeDefault