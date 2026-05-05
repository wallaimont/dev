import { useState, useCallback, useEffect } from 'react';
import { SKILLS } from '../constants/app.js';
import { loadCustomSkillsForUser, saveCustomSkillsForUser } from '../utils/storage.js';

export function useSkills(user) {
  const [customSkills, setCustomSkills]     = useState([]);
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [newSkillName, setNewSkillName]     = useState('');
  const [newSkillPrompt, setNewSkillPrompt] = useState('');

  // Reload custom skills when the authenticated user changes
  useEffect(() => {
    if (user) {
      setCustomSkills(loadCustomSkillsForUser(user));
    } else {
      setCustomSkills([]);
      setSelectedSkills([]);
    }
  }, [user]);

  const addCustomSkill = useCallback(() => {
    if (!newSkillName.trim() || !newSkillPrompt.trim()) return;
    const skill = {
      id:     crypto.randomUUID(),
      label:  newSkillName.trim(),
      prompt: newSkillPrompt.trim(),
      icon:   '🧩',
    };
    const updated = [...customSkills, skill];
    setCustomSkills(updated);
    saveCustomSkillsForUser(user, updated);
    setNewSkillName('');
    setNewSkillPrompt('');
  }, [customSkills, newSkillName, newSkillPrompt, user]);

  const removeCustomSkill = useCallback((id) => {
    const updated = customSkills.filter(s => s.id !== id);
    setCustomSkills(updated);
    saveCustomSkillsForUser(user, updated);
    setSelectedSkills(prev => prev.filter(k => k !== `custom:${id}`));
  }, [customSkills, user]);

  /**
   * Returns the metadata (label, icon, prompt) for a skill key.
   * Built-in skills use plain keys (e.g. 'planning'), custom ones use 'custom:<id>'.
   */
  const getSkillMeta = useCallback((key) => {
    if (key.startsWith('custom:')) {
      const id = key.slice(7);
      return customSkills.find(s => s.id === id) ?? null;
    }
    return SKILLS[key] ?? null;
  }, [customSkills]);

  return {
    customSkills,
    selectedSkills,
    setSelectedSkills,
    newSkillName,
    setNewSkillName,
    newSkillPrompt,
    setNewSkillPrompt,
    addCustomSkill,
    removeCustomSkill,
    getSkillMeta,
  };
}
