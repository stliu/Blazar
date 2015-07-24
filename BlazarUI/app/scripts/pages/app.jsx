import React, {Component} from 'react';
import {RouteHandler} from 'react-router';
import Navigation from '../components/navigation/navigation.jsx';
import ProjectsSidebarContainer from '../components/sidebar/ProjectsSidebarContainer.jsx';

class App extends Component{

  render() {
    return (
      <div>
        <Navigation />
        <div className="page-wrapper">
          <ProjectsSidebarContainer/>
          <RouteHandler/>
        </div>
      </div>
    );
  }

}

export default App;
