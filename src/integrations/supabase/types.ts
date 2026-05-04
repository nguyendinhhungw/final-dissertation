export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[]

export type Database = {
  // Allows to automatically instantiate createClient with right options
  // instead of createClient<Database, { PostgrestVersion: 'XX' }>(URL, KEY)
  __InternalSupabase: {
    PostgrestVersion: "14.5"
  }
  public: {
    Tables: {
      applications: {
        Row: {
          admin_notes: string | null
          cover_letter: string | null
          created_at: string
          cv_path: string | null
          email: string
          full_name: string
          id: string
          job_id: string
          phone: string | null
          status: string
          updated_at: string
          user_id: string | null
        }
        Insert: {
          admin_notes?: string | null
          cover_letter?: string | null
          created_at?: string
          cv_path?: string | null
          email: string
          full_name: string
          id?: string
          job_id: string
          phone?: string | null
          status?: string
          updated_at?: string
          user_id?: string | null
        }
        Update: {
          admin_notes?: string | null
          cover_letter?: string | null
          created_at?: string
          cv_path?: string | null
          email?: string
          full_name?: string
          id?: string
          job_id?: string
          phone?: string | null
          status?: string
          updated_at?: string
          user_id?: string | null
        }
        Relationships: [
          {
            foreignKeyName: "applications_job_id_fkey"
            columns: ["job_id"]
            isOneToOne: false
            referencedRelation: "jobs"
            referencedColumns: ["id"]
          },
        ]
      }
      blog_posts: {
        Row: {
          author: string | null
          body_en: string | null
          body_vi: string | null
          category: string | null
          cover_url: string | null
          created_at: string
          display_order: number
          excerpt_en: string | null
          excerpt_vi: string | null
          id: string
          is_featured: boolean
          is_published: boolean
          published_at: string
          slug: string
          tags: string[] | null
          title_en: string
          title_vi: string
          updated_at: string
          views: number
        }
        Insert: {
          author?: string | null
          body_en?: string | null
          body_vi?: string | null
          category?: string | null
          cover_url?: string | null
          created_at?: string
          display_order?: number
          excerpt_en?: string | null
          excerpt_vi?: string | null
          id?: string
          is_featured?: boolean
          is_published?: boolean
          published_at?: string
          slug: string
          tags?: string[] | null
          title_en: string
          title_vi: string
          updated_at?: string
          views?: number
        }
        Update: {
          author?: string | null
          body_en?: string | null
          body_vi?: string | null
          category?: string | null
          cover_url?: string | null
          created_at?: string
          display_order?: number
          excerpt_en?: string | null
          excerpt_vi?: string | null
          id?: string
          is_featured?: boolean
          is_published?: boolean
          published_at?: string
          slug?: string
          tags?: string[] | null
          title_en?: string
          title_vi?: string
          updated_at?: string
          views?: number
        }
        Relationships: []
      }
      contacts: {
        Row: {
          admin_notes: string | null
          created_at: string
          email: string
          id: string
          is_read: boolean
          message: string
          name: string
          phone: string | null
          subject: string | null
        }
        Insert: {
          admin_notes?: string | null
          created_at?: string
          email: string
          id?: string
          is_read?: boolean
          message: string
          name: string
          phone?: string | null
          subject?: string | null
        }
        Update: {
          admin_notes?: string | null
          created_at?: string
          email?: string
          id?: string
          is_read?: boolean
          message?: string
          name?: string
          phone?: string | null
          subject?: string | null
        }
        Relationships: []
      }
      jobs: {
        Row: {
          benefits_en: string | null
          benefits_vi: string | null
          created_at: string
          department: string | null
          description_en: string | null
          description_vi: string | null
          display_order: number
          employment_type: string | null
          id: string
          is_open: boolean
          location: string | null
          requirements_en: string | null
          requirements_vi: string | null
          salary_range: string | null
          short_en: string | null
          short_vi: string | null
          slug: string
          title_en: string
          title_vi: string
          updated_at: string
        }
        Insert: {
          benefits_en?: string | null
          benefits_vi?: string | null
          created_at?: string
          department?: string | null
          description_en?: string | null
          description_vi?: string | null
          display_order?: number
          employment_type?: string | null
          id?: string
          is_open?: boolean
          location?: string | null
          requirements_en?: string | null
          requirements_vi?: string | null
          salary_range?: string | null
          short_en?: string | null
          short_vi?: string | null
          slug: string
          title_en: string
          title_vi: string
          updated_at?: string
        }
        Update: {
          benefits_en?: string | null
          benefits_vi?: string | null
          created_at?: string
          department?: string | null
          description_en?: string | null
          description_vi?: string | null
          display_order?: number
          employment_type?: string | null
          id?: string
          is_open?: boolean
          location?: string | null
          requirements_en?: string | null
          requirements_vi?: string | null
          salary_range?: string | null
          short_en?: string | null
          short_vi?: string | null
          slug?: string
          title_en?: string
          title_vi?: string
          updated_at?: string
        }
        Relationships: []
      }
      notifications: {
        Row: {
          created_at: string
          id: string
          is_read: boolean
          link: string | null
          message: string
          title: string
          user_id: string
        }
        Insert: {
          created_at?: string
          id?: string
          is_read?: boolean
          link?: string | null
          message: string
          title: string
          user_id: string
        }
        Update: {
          created_at?: string
          id?: string
          is_read?: boolean
          link?: string | null
          message?: string
          title?: string
          user_id?: string
        }
        Relationships: []
      }
      portfolio_projects: {
        Row: {
          body_en: string | null
          body_vi: string | null
          category: string | null
          cover_url: string | null
          created_at: string
          display_order: number
          gallery: Json | null
          id: string
          is_published: boolean
          short_en: string | null
          short_vi: string | null
          slug: string
          tech_stack: string[] | null
          title_en: string
          title_vi: string
          updated_at: string
        }
        Insert: {
          body_en?: string | null
          body_vi?: string | null
          category?: string | null
          cover_url?: string | null
          created_at?: string
          display_order?: number
          gallery?: Json | null
          id?: string
          is_published?: boolean
          short_en?: string | null
          short_vi?: string | null
          slug: string
          tech_stack?: string[] | null
          title_en: string
          title_vi: string
          updated_at?: string
        }
        Update: {
          body_en?: string | null
          body_vi?: string | null
          category?: string | null
          cover_url?: string | null
          created_at?: string
          display_order?: number
          gallery?: Json | null
          id?: string
          is_published?: boolean
          short_en?: string | null
          short_vi?: string | null
          slug?: string
          tech_stack?: string[] | null
          title_en?: string
          title_vi?: string
          updated_at?: string
        }
        Relationships: []
      }
      profiles: {
        Row: {
          avatar_url: string | null
          created_at: string
          display_name: string | null
          id: string
          phone: string | null
          updated_at: string
          user_id: string
        }
        Insert: {
          avatar_url?: string | null
          created_at?: string
          display_name?: string | null
          id?: string
          phone?: string | null
          updated_at?: string
          user_id: string
        }
        Update: {
          avatar_url?: string | null
          created_at?: string
          display_name?: string | null
          id?: string
          phone?: string | null
          updated_at?: string
          user_id?: string
        }
        Relationships: []
      }
      services: {
        Row: {
          body_en: string | null
          body_vi: string | null
          created_at: string
          display_order: number
          icon: string | null
          id: string
          image_url: string | null
          is_published: boolean
          short_en: string | null
          short_vi: string | null
          slug: string
          title_en: string
          title_vi: string
          updated_at: string
        }
        Insert: {
          body_en?: string | null
          body_vi?: string | null
          created_at?: string
          display_order?: number
          icon?: string | null
          id?: string
          image_url?: string | null
          is_published?: boolean
          short_en?: string | null
          short_vi?: string | null
          slug: string
          title_en: string
          title_vi: string
          updated_at?: string
        }
        Update: {
          body_en?: string | null
          body_vi?: string | null
          created_at?: string
          display_order?: number
          icon?: string | null
          id?: string
          image_url?: string | null
          is_published?: boolean
          short_en?: string | null
          short_vi?: string | null
          slug?: string
          title_en?: string
          title_vi?: string
          updated_at?: string
        }
        Relationships: []
      }
      site_content: {
        Row: {
          description: string | null
          id: string
          key: string
          updated_at: string
          value_en: string | null
          value_vi: string | null
        }
        Insert: {
          description?: string | null
          id?: string
          key: string
          updated_at?: string
          value_en?: string | null
          value_vi?: string | null
        }
        Update: {
          description?: string | null
          id?: string
          key?: string
          updated_at?: string
          value_en?: string | null
          value_vi?: string | null
        }
        Relationships: []
      }
      user_roles: {
        Row: {
          created_at: string
          id: string
          role: Database["public"]["Enums"]["app_role"]
          user_id: string
        }
        Insert: {
          created_at?: string
          id?: string
          role: Database["public"]["Enums"]["app_role"]
          user_id: string
        }
        Update: {
          created_at?: string
          id?: string
          role?: Database["public"]["Enums"]["app_role"]
          user_id?: string
        }
        Relationships: []
      }
    }
    Views: {
      [_ in never]: never
    }
    Functions: {
      has_role: {
        Args: {
          _role: Database["public"]["Enums"]["app_role"]
          _user_id: string
        }
        Returns: boolean
      }
    }
    Enums: {
      app_role: "admin" | "user" | "hr"
    }
    CompositeTypes: {
      [_ in never]: never
    }
  }
}

type DatabaseWithoutInternals = Omit<Database, "__InternalSupabase">

type DefaultSchema = DatabaseWithoutInternals[Extract<keyof Database, "public">]

export type Tables<
  DefaultSchemaTableNameOrOptions extends
    | keyof (DefaultSchema["Tables"] & DefaultSchema["Views"])
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
        DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])
    : never = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
      DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])[TableName] extends {
      Row: infer R
    }
    ? R
    : never
  : DefaultSchemaTableNameOrOptions extends keyof (DefaultSchema["Tables"] &
        DefaultSchema["Views"])
    ? (DefaultSchema["Tables"] &
        DefaultSchema["Views"])[DefaultSchemaTableNameOrOptions] extends {
        Row: infer R
      }
      ? R
      : never
    : never

export type TablesInsert<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Insert: infer I
    }
    ? I
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Insert: infer I
      }
      ? I
      : never
    : never

export type TablesUpdate<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Update: infer U
    }
    ? U
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Update: infer U
      }
      ? U
      : never
    : never

export type Enums<
  DefaultSchemaEnumNameOrOptions extends
    | keyof DefaultSchema["Enums"]
    | { schema: keyof DatabaseWithoutInternals },
  EnumName extends DefaultSchemaEnumNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"]
    : never = never,
> = DefaultSchemaEnumNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"][EnumName]
  : DefaultSchemaEnumNameOrOptions extends keyof DefaultSchema["Enums"]
    ? DefaultSchema["Enums"][DefaultSchemaEnumNameOrOptions]
    : never

export type CompositeTypes<
  PublicCompositeTypeNameOrOptions extends
    | keyof DefaultSchema["CompositeTypes"]
    | { schema: keyof DatabaseWithoutInternals },
  CompositeTypeName extends PublicCompositeTypeNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"]
    : never = never,
> = PublicCompositeTypeNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"][CompositeTypeName]
  : PublicCompositeTypeNameOrOptions extends keyof DefaultSchema["CompositeTypes"]
    ? DefaultSchema["CompositeTypes"][PublicCompositeTypeNameOrOptions]
    : never

export const Constants = {
  public: {
    Enums: {
      app_role: ["admin", "user", "hr"],
    },
  },
} as const
