package net.sf.regadb.db.login;

public class DbException extends RuntimeException {
  public DbException(String msg) {
    super(msg);
  }

  public DbException(String msg, Throwable t) {
    super(msg + t.getMessage(), t);
  }
}
