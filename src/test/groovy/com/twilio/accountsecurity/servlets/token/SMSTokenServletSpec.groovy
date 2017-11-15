package com.twilio.accountsecurity.servlets.token

import com.twilio.accountsecurity.exceptions.TokenVerificationException
import com.twilio.accountsecurity.services.TokenService
import com.twilio.accountsecurity.servlets.SessionManager
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SMSTokenServletSpec extends Specification {

    TokenService tokenService = Mock()
    SessionManager sessionManager = Mock()

    HttpServletRequest request = Mock()
    HttpServletResponse response = Mock()
    PrintWriter responseWritter = Mock()

    @Subject def subject = new SMSTokenServlet(tokenService, sessionManager)

    def "doPost - return 500 for user not logged in"() {
        when:
        subject.doPost(request, response)

        then:
        1 * sessionManager.getLoggedUsername(request) >> Optional.empty()
        1 * response.setStatus(500)
        1 * response.getWriter() >> responseWritter
        1 * responseWritter.print("You are not logged in")
    }

    def "doPost - return 500 for TokenVerificationException"() {
        when:
        subject.doPost(request, response)

        then:
        1 * sessionManager.getLoggedUsername(request) >> Optional.of('username')
        1 * tokenService.sendSmsToken('username') >> { throw new TokenVerificationException('message')}
        1 * response.setStatus(500)
        1 * response.getWriter() >> responseWritter
        1 * responseWritter.print('message')
    }

    def "doPost - return 200"() {
        when:
        subject.doPost(request, response)

        then:
        1 * sessionManager.getLoggedUsername(request) >> Optional.of('username')
        1 * tokenService.sendSmsToken('username')
        1 * response.setStatus(200)
    }
}
