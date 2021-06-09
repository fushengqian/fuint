package com.fuint.base.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/**
 * 会话管理控制类
 *
 * @author fsq
 * @version $Id: SessionController.java, v 0.1 2015年11月18日 下午3:05:46 fsq Exp $
 */
@Controller
@RequestMapping("/sessions")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    /**
     * sessionDao
     */
    @Autowired
    private SessionDAO sessionDAO;

    /**
     * 会话列表
     *
     * @param model
     * @return
     */
    @RequiresPermissions("sessions/query")
    @RequestMapping(value = "/query")
    public String list(Model model) {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        model.addAttribute("sessions", sessions);
        model.addAttribute("sessionCount", sessions.size());
        return "sessions/sessions_list";
    }

    /**
     * 强制退出会话
     *
     * @param sessionId 会话ID
     * @return
     */
    @RequiresPermissions("sessions/forceLogout")
    @RequestMapping("/forceLogout/{sessionId}")
    public String forceLogout(@PathVariable("sessionId") String sessionId) {
        try {
            Session session = sessionDAO.readSession(sessionId);
            if (session != null) {
                sessionDAO.delete(session);
            } else {
                logger.info("Session:{} 会话不存在!", sessionId);
            }
        } catch (Exception e) {
            logger.error("Session:{} 会话踢出发生异常,{}!", sessionId, e);
        }
        return "redirect:/sessions/query";
    }

}
