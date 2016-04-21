/*global config*/
import React, {Component, PropTypes} from 'react';
import Immutable from 'immutable'
import CardStack from '../shared/CardStack.jsx';
import RepoBranchCard from '../shared/RepoBranchCard.jsx';

import Headline from '../shared/headline/Headline.jsx';
import UIGrid from '../shared/grid/UIGrid.jsx';
import UIGridItem from '../shared/grid/UIGridItem.jsx';

import RepoBuildActions from '../../actions/repoBuildActions';
import RepoBuildStore from '../../stores/repoBuildStore';

let initialState = {
  expandedCard: -1,
  loading: false,
  repoBuild: {},
  moduleBuilds: []
};

class Dashboard extends Component {

  constructor(props) {
    super(props);

    this.state = initialState;
  }

  componentDidMount() {
    this.unsubscribeFromRepo = RepoBuildStore.listen(this.onStatusChange.bind(this));
  }

  componentWillUnmount() {
    this.unsubscribeFromRepo();
  }

  onStatusChange(state) {
    this.setState(state);
  }

  onCardClick(key, build) {
    if (key === this.state.expandedCard) {
      this.setState({
        expandedCard: -1,
        build: undefined
      });

      return;
    }

    this.setState({
      expandedCard: key,
      build: build,
      loading: true
    });

    const repoBuildId = build.has('inProgressBuild') ? build.get('inProgressBuild').get('id') : build.get('lastBuild').get('id');
    const branchId = build.get('gitInfo').get('id');
    RepoBuildActions.loadRepoBuildById(repoBuildId);
    RepoBuildActions.loadModuleBuildsById(repoBuildId, branchId);
  }

  renderCards() {
    const numberOfBuilds = this.props.starredBuilds.size;

    return this.props.starredBuilds.map((build, key) => {
      return (
        <RepoBranchCard
          onClick={this.onCardClick.bind(this, key, build)}
          key={key}
          expanded={key === this.state.expandedCard} 
          belowExpanded={key === this.state.expandedCard + 1 && this.state.expandedCard !== -1}
          first={key === 0}
          last={key === numberOfBuilds - 1}
          item={build} 
          {...this.state} />
      );
    })
  }

  render() {
    return (
      <UIGrid>                
        <UIGridItem size={12} className='dashboard-unit'>
          <Headline>
            Starred Branches
          </Headline>
          <CardStack>
            {this.renderCards()}
          </CardStack>
        </UIGridItem>
      </UIGrid>
    );
  }
}

Dashboard.propTypes = {
  starredBuilds: PropTypes.instanceOf(Immutable.List),
  loadingStars: PropTypes.bool,
  loadingBuilds: PropTypes.bool,
  params: PropTypes.object
};

export default Dashboard;
