package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest_NotIncludingEx {

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.requset();
        } catch (Exception e) {
//            e.printStackTrace();
            log.info("ex", e);
        }
    }

    @Test
    void unchecked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.requset()).isInstanceOf(Exception.class);
    }

    static class Controller {
        Service service = new Service();

        public void requset() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException();
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}