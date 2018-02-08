package com.wangwenjun.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wangwenjun.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018/1/4.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

        mCrimes = new ArrayList<Crime>();
        /*for(int i = 0; i < 3; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }*/
    }

    public List<Crime> getCrimes(){
        return mCrimes;
    }
    public Crime getCrime(UUID id){
        for(Crime crime:mCrimes){
            if(crime.getId().equals(id)){
                return crime;
            }
        }
        return  null;
    }

    /**
     * 添加菜单新建接口
     * @param c
     */
    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    /**
     * 添加菜单删除接口
     * @param id
     */
    public void deleteCrime(UUID id){
        for(Crime crime: mCrimes){
            if(id != null && crime.getId().equals(id)) {
                mCrimes.remove(crime);
                break;
            }
        }
    }
}
