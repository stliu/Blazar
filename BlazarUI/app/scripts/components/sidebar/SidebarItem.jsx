import React, {Component, PropTypes} from 'react';
import classnames from 'classnames';
import {has, contains} from 'underscore';
import {truncate} from '../Helpers.js';
import BuildingIcon from '../shared/BuildingIcon.jsx';
import Icon from '../shared/Icon.jsx';
import Star from '../shared/Star.jsx';

import {Link} from 'react-router'

class SidebarItem extends Component {

  getItemClasses() {
    return classnames([
      'sidebar-item',
      this.props.classNames
    ]);
  }
  
  renderRepoLink() {
    const {gitInfo} = this.props;
    const linkPath = `/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}`;
    
    return (
      <div className='sidebar-item__repo-link'>
        <Icon type='octicon' name='repo' classNames='icon-muted'/>{ ' ' }
        <Link to={linkPath} className='sidebar-item__module-branch-name'>
          {truncate(gitInfo.repository, 30, true)}
        </Link>
      </div>
    );
  }

  renderBranchLink() {
    const {gitInfo} = this.props;

    return (
      <div className='sidebar-item__branch-link'>
        <Icon type='octicon' name='git-branch' classNames='icon-muted'/>{ ' ' }
        <Link 
          to={gitInfo.blazarBranchPath}
          className='sidebar-item__module-branch-name'>
            {truncate(gitInfo.branch, 40, true)}
        </Link>
      </div>
    )
  }
  
  renderBuildLink() {
    const {buildType, build, hasBuild} = this.props;
    let icon, buildIdLink;
    
    if (hasBuild) {
      icon = (
        <Link to={build.blazarPath} className='sidebar-item__building-icon-link'>
          <BuildingIcon result={build.state} size='small' />
        </Link>
      );

      buildIdLink =(
        <Link to={build.blazarPath} className='sidebar-item__build-number'>
          #{build.id}
        </Link>
      );  
    }

    // never built on blazar
    else {
      icon = (
        <div className='sidebar-item__building-icon-link'>
          <BuildingIcon result='never-built' size='small' />
        </div>
      );
    }
    
    return (
      <div>
        {icon}
        {buildIdLink}
      </div>
    );
  }

  render() {    
    const {build, gitInfo} = this.props;

    return (
      <li className={this.getItemClasses()}>
        {this.renderBuildLink()}
        {this.renderRepoLink()}
        {this.renderBranchLink()}
      </li>
    )
  }
}

SidebarItem.propTypes = {
  isStarred: PropTypes.bool,
  build: PropTypes.object.isRequired
};

export default SidebarItem;
