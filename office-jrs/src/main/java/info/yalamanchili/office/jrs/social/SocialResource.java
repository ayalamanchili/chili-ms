package info.yalamanchili.office.jrs.social;

import info.yalamanchili.office.dao.social.SocialDao;
import info.yalamanchili.office.entity.social.Post;
import info.yalamanchili.office.social.SocialService;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ayalamanchili
 */
@Path("social")
@Component
@Transactional
@Produces("application/json")
@Consumes("application/json")
@Scope("request")
public class SocialResource {

    @Autowired
    public SocialDao socialDao;
    @Autowired
    public SocialService socialService;

    @GET
    @Path("/employeefeed/{start}/{limit}")
    public List<info.yalamanchili.office.dto.social.Post> getEmployeeFeed(@PathParam("start") int start, @PathParam("limit") int limit) {
        return socialService.getEmployeeFeed(start, limit);
    }

    @PUT
    @Path("/CreatePost")
    public Post CreatePost(Post NewPost) {

        return socialDao.CreatePost(NewPost);
    }

    @PUT
    @Path("/addreply/{parentPostId}")
    public Post addReply(@PathParam("parentPostId") Long parentPostId, Post reply) {
        return socialDao.addReply(parentPostId, reply);
    }
}
