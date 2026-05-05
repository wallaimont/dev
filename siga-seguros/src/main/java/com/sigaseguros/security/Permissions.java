package com.sigaseguros.security;

import com.sigaseguros.enums.Perfil;

import java.util.*;

/**
 * Matriz centralizada de permissões por perfil.
 * Cada módulo define quais perfis podem visualizar, criar/editar e excluir.
 */
public final class Permissions {

    private Permissions() {}

    // Módulos do sistema
    public static final String DASHBOARD    = "dashboard";
    public static final String CLIENTES     = "clientes";
    public static final String PROPOSTAS    = "propostas";
    public static final String APOLICES     = "apolices";
    public static final String RENOVACOES   = "renovacoes";
    public static final String SINISTROS    = "sinistros";
    public static final String FINANCEIRO   = "financeiro";
    public static final String COMISSOES    = "comissoes";
    public static final String SEGURADORAS  = "seguradoras";
    public static final String CORRETORAS   = "corretoras";
    public static final String USUARIOS     = "usuarios";
    public static final String AUDITORIA    = "auditoria";

    // Ações
    public static final String VIEW   = "view";
    public static final String CREATE = "create";
    public static final String EDIT   = "edit";
    public static final String DELETE = "delete";

    private static final Map<String, Map<String, Set<Perfil>>> MATRIX = new LinkedHashMap<>();

    static {
        define(DASHBOARD,   set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR, Perfil.FINANCEIRO), Set.of(), Set.of());
        define(CLIENTES,    set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL), set(Perfil.ADMIN, Perfil.GESTOR));
        define(PROPOSTAS,   set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL), set(Perfil.ADMIN, Perfil.GESTOR));
        define(APOLICES,    set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR, Perfil.OPERADOR),  set(Perfil.ADMIN, Perfil.GESTOR));
        define(RENOVACOES,  set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR));
        define(SINISTROS,   set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR, Perfil.OPERADOR),  Set.of());
        define(FINANCEIRO,  set(Perfil.ADMIN, Perfil.GESTOR, Perfil.FINANCEIRO), set(Perfil.ADMIN, Perfil.GESTOR, Perfil.FINANCEIRO), Set.of());
        define(COMISSOES,   set(Perfil.ADMIN, Perfil.GESTOR, Perfil.FINANCEIRO), set(Perfil.ADMIN, Perfil.GESTOR, Perfil.FINANCEIRO), Set.of());
        define(SEGURADORAS, set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR), set(Perfil.ADMIN));
        define(CORRETORAS,  set(Perfil.ADMIN, Perfil.GESTOR, Perfil.COMERCIAL, Perfil.OPERADOR), set(Perfil.ADMIN, Perfil.GESTOR), set(Perfil.ADMIN));
        define(USUARIOS,    set(Perfil.ADMIN), set(Perfil.ADMIN), set(Perfil.ADMIN));
        define(AUDITORIA,   set(Perfil.ADMIN, Perfil.AUDITOR), Set.of(), Set.of());
    }

    private static Set<Perfil> set(Perfil... perfis) {
        return EnumSet.copyOf(List.of(perfis));
    }

    private static void define(String module, Set<Perfil> view, Set<Perfil> create, Set<Perfil> delete) {
        Map<String, Set<Perfil>> actions = new LinkedHashMap<>();
        actions.put(VIEW, view);
        actions.put(CREATE, create);
        actions.put(EDIT, create); // edit = mesmos perfis do create
        actions.put(DELETE, delete);
        MATRIX.put(module, actions);
    }

    /**
     * Verifica se o perfil pode executar a ação no módulo.
     */
    public static boolean hasPermission(Perfil perfil, String module, String action) {
        Map<String, Set<Perfil>> actions = MATRIX.get(module);
        if (actions == null) return false;
        Set<Perfil> allowed = actions.get(action);
        return allowed != null && allowed.contains(perfil);
    }

    /**
     * Retorna os módulos que o perfil pode visualizar.
     */
    public static List<String> getModulesForPerfil(Perfil perfil) {
        List<String> modules = new ArrayList<>();
        for (Map.Entry<String, Map<String, Set<Perfil>>> entry : MATRIX.entrySet()) {
            Set<Perfil> viewers = entry.getValue().get(VIEW);
            if (viewers != null && viewers.contains(perfil)) {
                modules.add(entry.getKey());
            }
        }
        return modules;
    }

    /**
     * Retorna mapa de permissões para um perfil (módulo -> lista de ações permitidas).
     */
    public static Map<String, List<String>> getPermissionMap(Perfil perfil) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Set<Perfil>>> moduleEntry : MATRIX.entrySet()) {
            List<String> actions = new ArrayList<>();
            for (Map.Entry<String, Set<Perfil>> actionEntry : moduleEntry.getValue().entrySet()) {
                if (actionEntry.getValue().contains(perfil)) {
                    actions.add(actionEntry.getKey());
                }
            }
            if (!actions.isEmpty()) {
                result.put(moduleEntry.getKey(), actions);
            }
        }
        return result;
    }
}
