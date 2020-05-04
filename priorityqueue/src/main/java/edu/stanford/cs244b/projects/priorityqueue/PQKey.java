package edu.stanford.cs244b.projects.priorityqueue;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import org.springframework.lang.NonNull;


public class PQKey implements Comparable<PQKey>{

    private Long _priority;
    private UUID _uuid;

  public PQKey(Long priority, UUID uuid) {
    _priority = priority;
    _uuid = uuid;
  }

  public Long getPriority() {
    return _priority;
  }

  public UUID getUuid() {
    return _uuid;
  }

  @Override
  public int compareTo(@NonNull PQKey o) {
    return Comparator.comparingLong(PQKey::getPriority).thenComparing(PQKey::getUuid).compare(this, o);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PQKey pqKey = (PQKey) o;
    return Objects.equals(_priority, pqKey._priority) && Objects.equals(_uuid, pqKey._uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_priority, _uuid);
  }

  @Override
  public String toString() {
    return "PQKey{" + "_priority=" + _priority + ", _uuid=" + _uuid + '}';
  }
}