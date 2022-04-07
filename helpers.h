#ifndef HELPERS_H
#define HELPERS_H

// funny macro property automation
#define AUTO_PROPERTY(TYPE, NAME) \
    Q_PROPERTY(TYPE NAME READ NAME WRITE NAME NOTIFY NAME ## Changed ) \
    public: \
       TYPE NAME() const { return a_ ## NAME ; } \
       void NAME(TYPE value) { \
          if (a_ ## NAME == value) return; \
          a_ ## NAME = value; \
          emit NAME ## Changed(value); \
        } \
       Q_SIGNAL void NAME ## Changed(TYPE value);\
    private: \
       TYPE a_ ## NAME;

#endif // HELPERS_H
