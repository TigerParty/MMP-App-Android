package com.thetigerparty.argodflib.Model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fredtsao on 6/14/16.
 */
@Table(name = "localproject")
public class LocalProject extends Model{
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

    @Column(name = "container_id")
    public int container_id;

    @Column(name = "parent_id")
    public int parent_id;

    public static LocalProject getProject(int project_id){
        return new Select()
                .from(LocalProject.class)
                .where("project_id = ?", project_id)
                .executeSingle();
    }

    public static LocalProject getProject(int project_id, String project_type){
        return new Select()
                .from(LocalProject.class)
                .where("project_id = ? " +
                        "AND project_type = ?",
                        project_id, project_type)
                .executeSingle();
    }

    public static List<LocalProject> getAllProjects(){
        return new Select()
                .from(LocalProject.class)
                .execute();
    }

    public static List<LocalProject> getLocalProjects(String name){
        return new Select()
                .from(LocalProject.class)
                .leftJoin(District.class)
                .on("localproject.district_id = district.district_id")
                .leftJoin(Region.class)
                .on("district.region_id = region.region_id")
                .where("(localproject.title LIKE ? " +
                        "OR district.name = ? " +
                        "OR region.name = ?) " +
                        "AND project_type = 'new'",
                        '%'+name+'%', name, name)
                .groupBy("localproject.Id")
                .orderBy("LOWER(localproject.title) ASC")
                .execute();
    }

    public static void updateProjectTypeById(String project_type, int project_id){
        new Update(LocalProject.class)
                .set("project_type = ?", project_type)
                .where("project_id = ?", project_id)
                .execute();
    }

    public static void updateProjectIdById(int project_id, int server_project_id){
        new Update(LocalProject.class)
                .set("project_id = ?", server_project_id)
                .where("project_type = server AND project_id = ?", project_id)
                .execute();
    }

    public static void deleteProjectById(int project_id){
        new Delete()
                .from(LocalProject.class)
                .where("project_id = ? AND project_type = 'new'", project_id)
                .execute();
    }

    public static void deleteProjectById(String deleted_at, int project_id){
        new Update(LocalProject.class)
                .set("deleted_at = ?", deleted_at)
                .where("project_id = ? AND project_type = 'new'", project_id)
                .execute();
    }

    public static boolean hasProject (int project_id) {
        int count = new Select().from(LocalProject.class).where("project_id = ?", project_id).execute().size();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static List<Report> listReportsByLocalprojectId(int project_id, String project_type){
        return new Select()
                .from(Report.class)
                .where("project_id = ? AND project_type = ?", project_id, project_type)
                .execute();
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

    public static List<Region> getRegions(int project_id){
        List<RelationProjectBelongRegion> list_rel_regions = new Select()
                .from(RelationProjectBelongRegion.class)
                .where("project_id = ? AND project_type = ?", project_id, "new")
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

    public static List<LocalProject> getAllProjectTitle() {
        return new Select("title").from(LocalProject.class).execute();
    }

    public static List<LocalProject> getSearchRootProjects(String keyword) {
        return new Select(
                "localproject.project_id," +
                        "localproject.project_type," +
                        "localproject.title," +
                        "localproject.description," +
                        "localproject.container_id,"+
                        "localproject.parent_id," +
                        "localproject.default_form_id," +
                        "localproject.created_by," +
                        "localproject.created_at," +
                        "localproject.deleted_at"
        )
                .from(LocalProject.class)
                .leftJoin(RelationProjectBelongRegion.class)
                .on("relation_project_belong_region.project_id = localproject.project_id " +
                        "AND relation_project_belong_region.project_type = localproject.project_type")
                .leftJoin(Region.class)
                .on("relation_project_belong_region.region_id = region.region_id")
                .leftJoin(Container.class)
                .on("localproject.container_id = container.container_id")
                .where(  "(localproject.title LIKE ? OR region.name LIKE ?) " +
                         "AND localproject.parent_id = 0 " +
                         "AND container.parent_id = 0 " +
                         "AND localproject.deleted_at IS NULL ",
                         "%"+keyword+"%", "%"+keyword+"%"
                )
                .groupBy("localproject.project_id")
                .execute();
    }

    public static List<LocalProject> getSearchRootProjects(String keyword, int userId) {
        return new Select(
                    "localproject.project_id," +
                    "localproject.project_type," +
                    "localproject.title," +
                    "localproject.description," +
                    "localproject.container_id,"+
                    "localproject.parent_id," +
                    "localproject.default_form_id," +
                    "localproject.created_by," +
                    "localproject.created_at," +
                    "localproject.deleted_at"
                )
                .from(LocalProject.class)
                .leftJoin(RelationProjectBelongRegion.class)
                .on("relation_project_belong_region.project_id = localproject.project_id " +
                        "AND relation_project_belong_region.project_type = localproject.project_type")
                .leftJoin(Region.class)
                .on("relation_project_belong_region.region_id = region.region_id")
                .leftJoin(Container.class)
                .on("localproject.container_id = container.container_id")
                .where( "(localproject.title LIKE ? OR region.name LIKE ?) " +
                        "AND localproject.parent_id = 0 " +
                        "AND container.parent_id = 0 " +
                        "AND localproject.deleted_at IS NULL " +
                        "AND localproject.created_by = ? ",
                        "%"+keyword+"%", "%"+keyword+"%", userId
                )
                .groupBy("localproject.project_id")
                .execute();
    }

    public static List<LocalProject> getSearchProjects(String keyword, int container_id, int project_id, String project_type) {
        return new Select(
                    "localproject.project_id," +
                    "localproject.project_type," +
                    "localproject.title," +
                    "localproject.description," +
                    "localproject.container_id,"+
                    "localproject.parent_id," +
                    "localproject.default_form_id," +
                    "localproject.created_by," +
                    "localproject.created_at," +
                    "localproject.deleted_at"
                )
                .from(LocalProject.class)
                .leftJoin(RelationProjectBelongRegion.class)
                .on("relation_project_belong_region.project_id = localproject.project_id " +
                        "AND relation_project_belong_region.project_type = localproject.project_type")
                .leftJoin(Region.class)
                .on("relation_project_belong_region.region_id = region.region_id")
                .leftJoin(Container.class)
                .on("container.container_id = localproject.container_id")
                .leftJoin(Project.class)
                .on("project.project_id = localproject.parent_id")
                .where("(localproject.title LIKE ? OR region.name LIKE ?)" +
                        "AND localproject.parent_id = ? " +
                        "AND localproject.container_id = ? " +
                        "AND localproject.deleted_at IS NULL " +
                        "AND project.project_type = ?",
                        "%"+keyword+"%", "%"+keyword+"%", project_id, container_id, project_type)
                .groupBy("localproject.project_id")
                .execute();
    }

    public static boolean hasNotSubmitLocalProjectByOtherUser(int userId) {
        return new Select()
                .from(LocalProject.class)
                .where("created_by != ? ", userId)
                .execute().size() > 0;
    }

    public static void deleteLocalProjectByOtherUser(int userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String currentDateTime = sdf.format(new Date());
        ActiveAndroid.execSQL("UPDATE localproject SET deleted_at = \"" + currentDateTime + "\" WHERE created_by != " + userId );
    }
}