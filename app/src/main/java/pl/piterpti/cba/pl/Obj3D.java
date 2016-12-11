package pl.piterpti.cba.pl;

import android.content.Context;
import android.util.Log;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;

/**
 * Created by Piter on 11/12/2016.
 */
public class Obj3D {

    private String objName = "NN";
    private Object3D object3D;

    public Obj3D(Context context, TextureManager textureManager, int objRaw, int texRaw) {

        LoaderOBJ parser = new LoaderOBJ(context.getResources(), textureManager, objRaw);
        try {
            parser.parse();
            object3D = parser.getParsedObject();

            Material material = new Material();
            material.enableLighting(true);
            material.setDiffuseMethod(new DiffuseMethod.Lambert());
            material.setColor(0);

            Texture tex = new Texture("name", texRaw);

            material.addTexture(tex);

            object3D.setMaterial(material);


        } catch (ParsingException e) {
            Log.w("Excetpion", "Parsing exception: " + e.toString());
        } catch (ATexture.TextureException e2) {
            Log.w("Excetpion", "TEXTURE ERROR: " + e2.toString());
        }
    }

    public void setObjName(String newName) {
        objName = newName;
    }

    public Object3D getObject3D() {
        return object3D;
    }
}
