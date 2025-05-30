package com.bpmskm.projectgeoc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;

public class LanguageManagerTest {

    private Context mockContext;
    private SharedPreferences mockPrefs;
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        mockPrefs = mock(SharedPreferences.class);
        mockEditor = mock(SharedPreferences.Editor.class);

        // Konfiguracja mocków
        when(PreferenceManager.getDefaultSharedPreferences(mockContext)).thenReturn(mockPrefs);
        when(mockPrefs.getString(eq("Language.Manager.Selected"), anyString())).thenReturn("pl");
        when(mockPrefs.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
    }

    @Test
    public void testGetLanguageReturnsStoredLanguage() {
        String lang = LanguageManager.getLanguage(mockContext);
        assertEquals("pl", lang);
    }
}
