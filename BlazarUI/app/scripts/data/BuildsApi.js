/*global config*/
import { fromJS } from 'immutable';
import {has, contains} from 'underscore';
import humanizeDuration from 'humanize-duration';
import PollingProvider from '../services/PollingProvider';
import StarProvider from '../services/starProvider';

function _groupBuilds(builds) {
  const stars = StarProvider.getStars();
  
  let groupedBuilds = { all: builds };
  
  groupedBuilds.building = builds.filter((build) => {
    return has(build, 'inProgressBuild');
  });
  
  groupedBuilds.starred = builds.filter((build) => {
    return contains(stars, build.gitInfo.id);
  }) || [];

  return groupedBuilds;
}

function _parse(data) {
  const parsed = data.map((item) => {

    const {
      gitInfo,
      lastBuild,
      inProgressBuild
    } = item;

    if (has(item, 'inProgressBuild')) {
      item.inProgressBuild.blazarPath = `/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}/${gitInfo.branch}/${inProgressBuild.buildNumber}`.replace('#', '%23');
      item.inProgressBuild.duration = humanizeDuration(Date.now() - item.inProgressBuild.startTimestamp, {round: true, units: ['h', 'm', 's']});
    }

    if (has(item, 'lastBuild')) {
      item.lastBuild.duration = humanizeDuration(item.lastBuild.endTimestamp - item.lastBuild.startTimestamp, {round: true, units: ['h', 'm', 's']});
      item.lastBuild.blazarPath = `/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}/${gitInfo.branch}/${lastBuild.buildNumber}`.replace('#', '%23');
    }

    item.gitInfo.blazarRepositoryPath = `/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}`;
    item.gitInfo.blazarBranchPath = `/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}/${gitInfo.branch}`.replace('#', '%23');
    item.gitInfo.blazarHostPath = `/builds/${gitInfo.host}`;

    return item;
  });

  return parsed;
}

function fetchBuilds(cb) {
  
  if (this.buildsPoller) {
    this.buildsPoller.disconnect();
    this.buildsPoller = undefined;
  }

  this.buildsPoller = new PollingProvider({
    url: `${config.apiRoot}/branches/state`,
    type: 'GET',
    data: 'property=!pendingBuild.commitInfo&property=!inProgressBuild.commitInfo&property=!lastBuild.commitInfo&property=!pendingBuild.dependencyGraph&property=!inProgressBuild.dependencyGraph&property=!lastBuild.dependencyGraph&property=!pendingBuild.buildOptions&property=!inProgressBuild.buildOptions&property=!lastBuild.buildOptions&property=!pendingBuild.buildTrigger&property=!inProgressBuild.buildTrigger&property=!lastBuild.buildTrigger',
    dataType: 'json'
  });

  this.buildsPoller.poll((err, resp) => {
    if (err) {
      cb(err);
      return;
    }

    cb(err, _groupBuilds(_parse(resp)));
  });
}


function stopPolling() {
  if (!this.buildsPoller) {
    return;
  }

  this.buildsPoller.disconnect();
}

function fetchBuild(id) {

}

export default {
  fetchBuilds: fetchBuilds,
  fetchBuild: fetchBuild
};
