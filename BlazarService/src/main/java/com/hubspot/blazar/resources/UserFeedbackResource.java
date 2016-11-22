package com.hubspot.blazar.resources;

import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.hubspot.blazar.base.feedback.Feedback;
import com.hubspot.blazar.config.BlazarConfiguration;
import com.hubspot.blazar.config.BlazarSlackConfiguration;
import com.hubspot.blazar.exception.NotConfiguredException;
import com.hubspot.blazar.util.SlackUtils;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;

@Path("/user-feedback")
public class UserFeedbackResource {
  private static final Logger LOG = LoggerFactory.getLogger(UserFeedbackResource.class);

  private final Optional<SlackSession> slackSession;
  private final Optional<BlazarSlackConfiguration> blazarSlackConfiguration;

  @Inject
  public UserFeedbackResource(Optional<SlackSession> slackSession, BlazarConfiguration blazarConfiguration) {
    this.slackSession = slackSession;
    this.blazarSlackConfiguration = blazarConfiguration.getSlackConfiguration();
  }

  @POST
  public void handleFeedback(Feedback feedback) throws IOException {
    if (slackSession.isPresent() && blazarSlackConfiguration.isPresent()) {
      Optional<SlackChannel> channel = SlackUtils.getChannelByName(slackSession.get(), blazarSlackConfiguration.get().getFeedbackRoom().get());
      if (!blazarSlackConfiguration.get().getFeedbackRoom().isPresent() || !channel.isPresent()) {
        throw new NotConfiguredException("To use the feedback endpoint the feedbackRoom needs to be set in BlazarSlackConfiguration");
      }
      String title = String.format("New Feedback from %s", feedback.getUsername());
      String fallback = String.format("New Feedback from %s: %s", feedback.getUsername(), feedback.getMessage());

      SlackAttachment attachment = new SlackAttachment(title, fallback, "", "");
      attachment.addField("Feedback:", feedback.getMessage(), false);
      attachment.setTitleLink(feedback.getPage());

      slackSession.get().sendMessage(channel.get(), "", attachment);
    } else {
      LOG.warn("Received feedback but Slack is not configured: {}", feedback);
      throw new NotConfiguredException("To use the feedback endpoint the slack needs to be set in BlazarSlackConfiguration");
    }
  }
}
