import groovyx.net.http.EncoderRegistry
import groovyx.net.http.RESTClient
import org.cyberneko.html.parsers.SAXParser

import static groovyx.net.http.ContentType.TEXT


def getHtmlParser() {
    def nekoParser = new SAXParser().with {
        setFeature 'http://xml.org/sax/features/namespaces', false
        it
    }
    new XmlParser(nekoParser)
}

def client = new RESTClient('http://www.resgain.net/xmdq.html').with {
    client.params.setParameter(
        org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY,
        new org.apache.http.HttpHost("127.0.0.1", 8888)
    )
    encoderRegistry = new EncoderRegistry(charset: 'GBK')
    it
}

def path = 'http://www.resgain.net/xmdq.html'
def args = [
    requestContentType: TEXT,
    contentType       : TEXT,
    path              : path,
    body              : [

    ]
]

def xs = client.post args, { resp, text ->
    def divs = htmlParser.parse(text).depthFirst().DIV.findAll { div ->
        div.@class == 'col-xs-12'
    }
    divs[0].A.collect { a ->
        [a.@href, a.text()]
    }
}


def dir = 'E:/names' as File
if (!dir) {
    dir.mkdir()
}

println xs

xs.collect { x, n ->
    args = [
        requestContentType: TEXT,
        contentType       : TEXT,
        path              : x,
        body              : []
    ]

    def client1 = new RESTClient(x).with {
        it.client.params.setParameter(
            org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY,
            new org.apache.http.HttpHost("127.0.0.1", 8888)
        )
        encoderRegistry = new EncoderRegistry(charset: 'GBK')
        it
    }

    def names = client1.post args, { resp, text ->

        def divs = htmlParser.parse(text).depthFirst().DIV.findAll { div ->
            div.@class == 'col-xs-12'
        }
        divs[2].A.collect { a ->
            [a.text()]
        }

    }
    def wf = (dir.absolutePath + '/' + n) as File
    if (!wf) {
        wf.createNewFile()
    }
    def w = new FileWriter(wf)
    names.each {
        w.println it[0]
        w.flush()
    }
    w.close()

}


