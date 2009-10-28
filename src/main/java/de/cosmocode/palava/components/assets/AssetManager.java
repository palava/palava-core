package de.cosmocode.palava.components.assets;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.Content;
import de.cosmocode.palava.StreamContent;
import de.cosmocode.palava.components.cstore.ContentStore;
import de.cosmocode.palava.components.logging.Operation;
import de.cosmocode.palava.components.logging.PalavaLogger;

public class AssetManager  {
    
    private final ContentStore store;
    private final Session session;
    
    private static final Logger log = LoggerFactory.getLogger(AssetManager.class);
    private static final PalavaLogger palava = PalavaLogger.getLogger();

    public AssetManager(ContentStore store, Session session) {
        this.store = store;
        this.session = session;
    }

    public Asset getAsset( Long id, boolean withContent ) throws Exception {
        Asset asset = (Asset) session.load(Asset.class, id);

        if ( asset != null && withContent ) {
            StreamContent content = store.load(asset.getStoreKey());

            if ( content == null ) {
                log.error("content not found: {}", asset.getStoreKey());
                return null;
            } else
                asset.setContent(content);
        }
        
        return asset;
    }
    
    public Asset loadAssetContent (final Asset asset) throws Exception {
        if (asset != null) {
            final StreamContent content = store.load(asset.getStoreKey());

            if ( content == null ) {
                log.error("content not found: {}", asset.getStoreKey());
                return null;
            } else {
                asset.setContent(content);
            }
        }
        
        return asset;
    }

    public void updateAsset( Asset asset ) throws Exception {
        Transaction tx = session.beginTransaction();

        try {
        	asset.setModificationDate(new Date());
            session.save(asset);
            palava.log(session, asset.getId(), Asset.class, Operation.UPDATE, null, asset);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            tx.rollback();

            throw e;
        }
    }

    public void createAsset( Asset asset ) throws Exception {
        Content content = asset.getContent();
        if ( content == null ) throw new NullPointerException("content");

        String key = store.store(content);

        Transaction tx = session.beginTransaction();
        
        try {
            asset.setStoreKey(key);
            session.save(asset);            
            palava.log(session, asset.getId(), Asset.class, Operation.INSERT, null, asset);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            store.remove(key);
            tx.rollback();
            asset.setStoreKey(null);

            throw e;
        }
    }

    public Boolean removeAssetById (Long id) throws Exception {
        Asset asset = this.getAsset (id, false);
        
        Set<Long> dirIds = this.getDirectoryIdsForAsset (id).keySet();
        for (Long dirId : dirIds) {
            // if deletion on any directory that contains this asset fails for any reason,
            // then abort the whole deletion of this asset at once
            log.info("Currently removing asset with id {} from directory with id {}", id, dirId);
            Directory directory = getDirectory (dirId);
            if ( ! directory.removeAsset(asset) )
                return Boolean.FALSE;
            session.saveOrUpdate (directory);
            palava.log(session, directory.getId(), Directory.class, Operation.UPDATE, null, directory);
        }

        Transaction tx = session.beginTransaction();
        try {
            this.removeAsset (asset);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            tx.rollback();
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
            

    public void removeAsset( Asset asset ) {
            session.delete(asset);
            palava.log(session, asset.getId(), Asset.class, Operation.DELETE);
            store.remove(asset.getStoreKey());
    }

    public void createDirectory( Directory dir ) throws Exception {
        session.save(dir);
        palava.log(session, dir.getId(), Directory.class, Operation.INSERT, null, dir);
    }

    public void updateDirectory( Directory dir ) throws Exception {
        session.update(dir);
        palava.log(session, dir.getId(), Directory.class, Operation.UPDATE, null, dir);
    }

    public Directory getDirectory( Long id ) throws Exception {
        return (Directory) session.load(Directory.class, id);
    }
    
    /**
     * get a list of all directories containing a given asset
     * 
     * @param assetId the id of the asset this method is looking for
     * @return an id/name-list of all directories containing
     * the asset with the given assetID
     * @author schoenborn
     */
	@SuppressWarnings("unchecked")
	public Map<Long, String> getDirectoryIdsForAsset(Long assetId) {
    	
    	Map<Long, String> map = new HashMap<Long, String>();
    	
    	Query query = session.
    		getNamedQuery("directoriesByAssetId").
    		setLong("assetId", assetId);
    	
    	List<Object[]> list = query.list();
    	
    	for(Object[] array : list) {
			map.put((Long) array[0], (String) array[1]);
    	}
    	
    	return map;
    }
    
    public void removeDirectory ( Directory dir ) {
        session.delete(dir);
    }

    /** removes an asset from directory
     *
     * @return true if asset was found and deleted, false if not found
     */
    public Boolean removeAssetFromDirectory ( Long directoryId, Long assetId) throws Exception {
        Directory directory = (Directory) session.load(Directory.class, directoryId);
        Asset asset = (Asset) session.load(Asset.class, assetId);
        if ( ! directory.removeAsset(asset) )
            return Boolean.FALSE;
        

        Transaction tx = session.beginTransaction();

        try {
            session.save(directory);
            palava.log(session, directoryId, Directory.class, Operation.UPDATE, "removing asset " + assetId, directory);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            tx.rollback();
            throw e;
        }

        return Boolean.TRUE;
    }
    public Directory addAssetToDirectory ( long directoryId, long assetId) throws Exception {

        Directory directory = (Directory) session.load(Directory.class, directoryId);

        if (directory == null) 
        	throw new NullPointerException("Directory not found");

        Asset asset = (Asset) session.get(Asset.class, assetId);
        if (asset == null)  throw new NullPointerException ("Asset not found");  //createAsset( asset );

        directory.addAsset(asset);

        Transaction tx = session.beginTransaction();

        try {
            session.saveOrUpdate(directory);
            palava.log(session, directoryId, Directory.class, Operation.UPDATE, "adding asset " + assetId, directory);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            tx.rollback();
            throw e;
        }

        return directory;
    }
    public Directory addAssetToDirectory ( Long directoryId, String name, Long assetId) throws Exception {
        Directory directory = null;
        if (directoryId != null)
            directory = (Directory) session.load(Directory.class, directoryId);

        boolean newDir = directory == null;
        
        if (newDir) {
            directory = new Directory();
            directory.setName(name);
        }

        Asset asset = (Asset) session.get(Asset.class, assetId);
        if (asset == null)  throw new NullPointerException ("Asset not found");  //createAsset( asset );

        directory.addAsset(asset);

        Transaction tx = session.beginTransaction();

        try {
            session.saveOrUpdate(directory);
            if (newDir) {
                palava.log(session, directoryId, Directory.class, Operation.INSERT, null, directory);
            }
            palava.log(session, directoryId, Directory.class, Operation.UPDATE, "adding asset " + assetId, directory);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            tx.rollback();
            throw e;
        }

        return directory;
    }

    /**
     * Fill a directory with all assets, defined
     * by those IDs in the list. Afterwards, the
     * directory asset list will only consist of
     * the given entries.
     *
     * @author  Tobias Sarnowski
     * @param   assetIds    a list of all Ids of assets, the directory should hold
     * @param   directory   the directory to fill in the new Ids
     * @return  the used directory
     * @see     useAssetlistForDirectory(java.util.List, long)
     * @see     useAssetlistForDirectory(java.util.List, java.lang.String)
     */
    public Directory useAssetlistForDirectory(List<Long> assetIds, Directory directory) throws Exception
    {
        List<Asset> assets = new LinkedList<Asset>();
        for (Iterator<Long> i = assetIds.iterator(); i.hasNext();) {
            assets.add((Asset)session.load(Asset.class, i.next()));
        }
        directory.setAssets(assets);
        Transaction tx = session.beginTransaction();
        try {
            session.save(directory);
            
            for (Long assetID : assetIds) {
                palava.log(session, directory.getId(), Directory.class, Operation.UPDATE, "adding asset " + assetID, directory);
            }
            palava.log(session, directory.getId(), Directory.class, Operation.UPDATE, null, directory);
            session.flush();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
        return directory;
    }

    /**
     * Use the directory, specified by the Id to fill in the list.
     *
     * @author  Tobias Sarnowski
     * @param   assetIds    a list of all Ids of assets, the directory should hold
     * @param   directoryId the directory id of the directory to fill in the new Ids
     * @return  the used directory
     * @see     useAssetlistForDirectory(java.util.List, de.cosmocode.palava.components.assets.Directory)
     */
    public Directory useAssetlistForDirectory(List<Long> assetIds, long directoryId) throws Exception
    {
        Directory directory = (Directory)session.load(Directory.class, directoryId);
        return this.useAssetlistForDirectory(assetIds, directory);
    }

    /**
     * Use the name to create a new directory and fill in the list.
     *
     * @author  Tobias Sarnowski
     * @param   assetIds    a list of all Ids of assets, the directory should hold
     * @param   directoryName   the directory name of the new directory
     * @return  the used directory
     * @see     useAssetlistForDirectory(java.util.List, de.cosmocode.palava.components.assets.Directory)
     */
    public Directory useAssetlistForDirectory(List<Long> assetIds, String directoryName) throws Exception
    {
        Directory directory = new Directory();
        directory.setName(directoryName);
        Transaction tx = session.beginTransaction();
        try {
            session.save(directory);
            palava.log(session, directory.getId(), Directory.class, Operation.INSERT, null, directory);
            session.flush();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
        return this.useAssetlistForDirectory(assetIds, directory);
    }


}
