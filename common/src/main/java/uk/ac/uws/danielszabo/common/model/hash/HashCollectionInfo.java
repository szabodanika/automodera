package uk.ac.uws.danielszabo.common.model.hash;

import java.sql.Date;
import java.util.List;

public interface HashCollectionInfo {
  String getId();

  String getName();

  int getVersion();

  Date getCreated();

  Date getUpdated();

  String getDescription();

  boolean isEnabled();

  List<Topic> getTopicList();
}
