package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.Object.UserObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "project")
public class Project extends Model {
    @Column(name = "project_id")
    public int project_id;

    @Column(name = "title")
    public String title;

    @Column(name = "description")
    public String description;

    @Column(name = "default_form_id")
    public int default_form_id;

    @Column(name = "district_id")
    public int district_id;

    @Column(name = "created_by")
    public int created_by;

    @Column(name = "created_at")
    public String created_at;

    @Column(name = "deleted_at")
    public String deleted_at;

    @Column(name = "project_type")
    public String project_type;

    @Column(name = "parent_id")
    public int parent_id;

    @Column(name = "container_id")
    public int container_id;

    @Column(name = "edit_level_id")
    public int edit_level_id;

    public static Project selectSingle(int project_id, String project_type){
        return new Select()
                .from(Project.class)
                .where("project_id = ? AND project_type = ?", project_id, project_type)
                .executeSingle();
    }

    public static Project getLastProject() {
        return new Select()
                .from(Project.class)
                .orderBy("project_id DESC")
                .executeSingle();
    }

    public static List<Project> projects(String name){
        return new Select()
                .from(Project.class)
                .leftJoin(District.class)
                .on("project.district_id = district.district_id")
                .leftJoin(Region.class)
                .on("district.region_id = region.region_id")
                .where("(project.title LIKE ? OR " +
                       "district.name = ? OR " +
                       "region.name = ?) AND deleted_at IS NULL", '%' + name + '%', name, name)
                .orderBy("LOWER(project.title) ASC")
                .execute();
    }

    public static List<Project> getAllProjectTitle(){
        return new Select("title")
                .from(Project.class)
                .where("deleted_at IS NULL")
                .execute();
    }

    public static List<Project> getProjectsWithoutServerID(){
        return new Select()
                .from(Project.class)
                .where("server_project_id = 0")
                .execute();
    }

    public static void deleteWithServerID(){
        new Delete()
                .from(Project.class)
                .where("server_project_id != 0")
                .execute();
    }

    public static void deleteTable(){
        new Delete()
                .from(Project.class)
                .execute();
    }

    public static void deleteSingleProject(int project_id, String project_type){
        new Delete()
                .from(Project.class)
                .where("project_id = ? AND project_type = ?", project_id, project_type)
                .execute();
    }

    public static void updateProjectTypeById(String project_type, int project_id){
        new Update(Project.class)
                .set("project_type = ?", project_type)
                .where("project_id = ?", project_id)
                .execute();
    }

    public static void updateProjectIdById(int project_id, int server_project_id){
        new Update(Project.class)
                .set("project_id = ?", server_project_id)
                .where("project_type = 'server' AND project_id = ?", project_id)
                .execute();
    }

    public static boolean hasProject (int project_id){
        int count = new Select().from(Project.class).where("project_id = ? AND project_type = 'server'", project_id).execute().size();
        if(count > 0){
            return true;
        }
        else {
            return false;
        }
    }

    public static void attach_region(int project_id, String project_type, int region_id){
        RelationProjectBelongRegion rel = new RelationProjectBelongRegion();
        rel.project_id = project_id;
        rel.project_type = project_type;
        rel.region_id = region_id;
        rel.save();
    }

    public static void detach_region(int project_id, String project_type){
        new Delete()
                .from(RelationProjectBelongRegion.class)
                .where("project_id = ? AND project_type = ?", project_id, project_type)
                .execute();
    }

    public static List<Region> getRegions (int project_id) {
        List<RelationProjectBelongRegion> list_rel_regions = new Select()
                .from(RelationProjectBelongRegion.class)
                .where("project_id = ? AND project_type = ?", project_id, "server")
                .orderBy("region_id")
                .execute();
        List<Region> list_regions = new ArrayList<>();
        if(!list_rel_regions.isEmpty()){
            for(RelationProjectBelongRegion rel : list_rel_regions){
                if(rel != null){
                    list_regions.add(Region.selectSingle(rel.region_id));
                }
            }
        }
        return list_regions;
    }

    public static List<Project> getSearchRootProjects(String keyword) {
        From orm = new Select(
                "project.project_id," +
                        "project.project_type," +
                        "project.title," +
                        "project.description," +
                        "project.container_id,"+
                        "project.parent_id," +
                        "project.default_form_id," +
                        "project.created_by," +
                        "project.created_at," +
                        "project.deleted_at "
        )
                .from(Project.class)
                .leftJoin(RelationProjectBelongRegion.class)
                .on("relation_project_belong_region.project_id = project.project_id " +
                        "AND relation_project_belong_region.project_type = project.project_type")
                .leftJoin(RelationUserOwnProject.class)
                .on("relation_user_own_project.project_id = project.project_id " +
                        "AND relation_user_own_project.project_type = project.project_type")
                .leftJoin(Region.class)
                .on("relation_project_belong_region.region_id = region.region_id")
                .leftJoin(Container.class)
                .on("project.container_id = container.container_id")
                .leftJoin(PermissionLevel.class)
                .on("permission_level.permission_id = project.edit_level_id")
                .where( "project.parent_id = 0 " +
                        "AND container.parent_id = 0 " +
                        "AND project.deleted_at IS NULL " +
                        "AND ( project.title LIKE ? OR region.name LIKE ? )",
                        "%"+keyword+"%", "%"+keyword+"%"
                )
                .groupBy("project.project_id")
                .orderBy("LOWER(project.title) ASC");
        return orm.execute();
    }

    public static List<Project> getSearchRootProjects(String keyword, int userId) {
        User user = User.get(userId);
        int userLevel = PermissionLevel.getPriorityById(user.permission_level_id);
        From orm = new Select(
                    "project.project_id," +
                    "project.project_type," +
                    "project.title," +
                    "project.description," +
                    "project.container_id,"+
                    "project.parent_id," +
                    "project.default_form_id," +
                    "project.created_by," +
                    "project.created_at," +
                    "project.deleted_at "
                )
                .from(Project.class)
                .leftJoin(RelationProjectBelongRegion.class)
                .on("relation_project_belong_region.project_id = project.project_id " +
                        "AND relation_project_belong_region.project_type = project.project_type")
                .leftJoin(RelationUserOwnProject.class)
                .on("relation_user_own_project.project_id = project.project_id " +
                        "AND relation_user_own_project.project_type = project.project_type")
                .leftJoin(Region.class)
                .on("relation_project_belong_region.region_id = region.region_id")
                .leftJoin(Container.class)
                .on("project.container_id = container.container_id")
                .leftJoin(PermissionLevel.class)
                .on("permission_level.permission_id = project.edit_level_id")
                .where( "project.parent_id = 0 " +
                        "AND container.parent_id = 0 " +
                        "AND project.deleted_at IS NULL " +
                        "AND ( project.title LIKE ? OR region.name LIKE ? )" +
                        "AND ( permission_level.priority >= ? OR relation_user_own_project.user_id = ? )",
                        "%"+keyword+"%", "%"+keyword+"%", userLevel, userId
                )
                .groupBy("project.project_id")
                .orderBy("LOWER(project.title) ASC");
        return orm.execute();
    }

    public static List<Project> getSearchProjects(String keyword, int container_id, int project_id, String project_type) {
        return new Select(
                    "project.project_id," +
                    "project.project_type," +
                    "project.title," +
                    "project.description," +
                    "project.container_id,"+
                    "project.parent_id," +
                    "project.default_form_id," +
                    "project.created_by," +
                    "project.created_at," +
                    "project.deleted_at"
                )
                .from(Project.class)
                .leftJoin(RelationProjectBelongRegion.class)
                .on("relation_project_belong_region.project_id = project.project_id " +
                        "AND relation_project_belong_region.project_type = project.project_type")
                .leftJoin(Region.class)
                .on("relation_project_belong_region.region_id = region.region_id")
                .leftJoin(Container.class)
                .on("container.container_id = project.container_id")
                .where("(project.title LIKE ? OR region.name LIKE ?) " +
                        "AND project.parent_id = ? " +
                        "AND project.container_id = ? " +
                        "AND project.deleted_at IS NULL " +
                        "AND project.project_type = ?",
                        "%"+keyword+"%", "%"+keyword+"%", project_id, container_id, project_type)
                .groupBy("project.project_id")
                .orderBy("LOWER(project.title) ASC")
                .execute();
    }
}
