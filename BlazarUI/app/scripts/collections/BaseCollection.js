/*global config*/
import {map, has} from 'underscore';
import Collection from './Collection';
import humanizeDuration from 'humanize-duration';

class BaseCollection extends Collection {

  parse() {
    this.addHelpers();
  }

  addHelpers() {

    return map(this.data, (item) => {

      const {
        gitInfo,
        module,
        lastBuild,
        inProgressBuild
      } = item;

      if (has(item, 'module')) {
        item.module.blazarPath = {
          module: `${config.appRoot}/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}/${gitInfo.branch}/${module.name}`,
          branch: `${config.appRoot}/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}/${gitInfo.branch}`,
          repo: `${config.appRoot}/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}`
        };
      }

      if (has(item, 'inProgressBuild')) {
        item.inProgressBuild.blazarPath = `${config.appRoot}/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}/${gitInfo.branch}/${module.name}/${inProgressBuild.buildNumber}`;
      }

      if (has(item, 'lastBuild')) {
        item.lastBuild.duration = humanizeDuration(item.lastBuild.endTimestamp - item.lastBuild.startTimestamp);
        item.lastBuild.blazarPath = `${config.appRoot}/builds/${gitInfo.host}/${gitInfo.organization}/${gitInfo.repository}/${gitInfo.branch}/${module.name}/${lastBuild.buildNumber}`;
      }

      return item;

    });

  }


}

export default BaseCollection;
