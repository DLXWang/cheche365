import groovy.sql.Sql
import org.slf4j.*
@Grapes([
    @Grab('org.slf4j:slf4j-simple:1.5.11'),
    @Grab('mysql:mysql-connector-java:5.1.42'),
    @GrabConfig(systemClassLoader = true)
])

def logger = LoggerFactory.getLogger('sql')

logger.info 'Initialize SQL'
Sql sql = Sql.newInstance(url:'jdbc:mysql://localhost:3306/cheche?characterEncoding=utf8', properties: [user:'cheche', password: 'cheche'] as Properties, driver:'com.mysql.jdbc.Driver')
logger.info "Got a SQL connection: $sql"

sql.rows('select * from channel').each{
    logger.info it.name
}



